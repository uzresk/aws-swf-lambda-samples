package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import java.util.Date;
import java.util.TimeZone;

import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClock;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.spring.CronDecorator;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class CronWorkflowImpl implements CronWorkflow {

    private static final int SECOND = 1000;

    /**
     * This is needed to keep the decider logic deterministic as using
     * System.currentTimeMillis() in your decider logic is not.
     * WorkflowClock.currentTimeMillis() should be used instead.
     */
    private final WorkflowClock clock;

    /**
     * Used to create new run of the Cron workflow to reset history. This allows
     * "infinite" workflows.
     */
    private final CronWorkflowSelfClient selfClient;

    private TryCatchFinally startCronTask = null;

    public CronWorkflowImpl() {
        this(new DecisionContextProviderImpl().getDecisionContext().getWorkflowClock(),
                new CronWorkflowSelfClientImpl());
    }

    public CronWorkflowImpl(final WorkflowClock clock,
            final CronWorkflowSelfClient selfClient) {
        this.clock = clock;
        this.selfClient = selfClient;
    }

    @Override
    public void startCron(final CronWorkflowOptions options) {

        startCronTask = new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                long startTime = clock.currentTimeMillis();
                Date expiration = new Date(
                        startTime + options.getContinueAsNewAfterSeconds() * SECOND);
                TimeZone tz = TimeZone.getTimeZone(options.getTimeZone());

                ConfigHelper configHelper = null;
                try {
                    configHelper = ConfigHelper.createConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                CronDecorator cronDecorator = new CronDecorator(
                        options.getCronExpression(), expiration, tz, clock);

                ChildWorkflowClient client =
                        cronDecorator.decorate(ChildWorkflowClient.class,
                                new ChildWorkflowClientFactoryImpl().getClient());

                // workflowにlambdaを実行できる権限を与えて実行する
                String defaultLambdaRoleArn = configHelper.getSwfLambdaRoleArn();
                StartWorkflowOptions startWorkflowOptions =
                        new StartWorkflowOptions().withLambdaRole(defaultLambdaRoleArn);
                Promise<String> waitPromise = client.helloLambda(startWorkflowOptions);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                selfClient.startCron(options, startWorkflowOptions, waitPromise);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                // TODO Auto-generated method stub

            }

            @Override
            protected void doFinally() throws Throwable {
                // TODO Auto-generated method stub
            }
        };
    }

    @Asynchronous
    private void terminateWorkflow(Promise<Void> waitFor) {
        System.out.println("terminate workflow");
        startCronTask.cancel(null);
        //        ConfigHelper helper = null;
        //        try {
        //            helper = ConfigHelper.createConfig();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        AmazonSimpleWorkflow wf = helper.createSWFClient();
        //        wf.terminateWorkflowExecution(
        //                new TerminateWorkflowExecutionRequest().withWorkflowId("swf-lambda").withDomain(helper.getDomain()));
        //        RequestCancelWorkflowExecutionRequest requestCancelWorkflowExecutionRequest =
        //                new RequestCancelWorkflowExecutionRequest().withDomain(helper.getDomain())
        //                        .withWorkflowId("swf-lambda");
        //        wf.requestCancelWorkflowExecution(requestCancelWorkflowExecutionRequest);

    }
}