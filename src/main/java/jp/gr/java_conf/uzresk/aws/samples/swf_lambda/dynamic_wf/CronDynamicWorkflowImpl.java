package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.dynamic_wf;

import java.util.Date;
import java.util.TimeZone;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.DynamicWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.DynamicWorkflowClientImpl;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClock;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.generic.GenericWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.spring.CronDecorator;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class CronDynamicWorkflowImpl implements CronDynamicWorkflow {

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
    private final CronDynamicWorkflowSelfClient selfClient;

    private TryCatchFinally startCronTask = null;

    public CronDynamicWorkflowImpl() {
        this(new DecisionContextProviderImpl().getDecisionContext().getWorkflowClock(),
                new CronDynamicWorkflowSelfClientImpl());
    }

    public CronDynamicWorkflowImpl(WorkflowClock clock,
            CronDynamicWorkflowSelfClient selfClient) {
        this.clock = clock;
        this.selfClient = selfClient;
    }

    @Override
    public void startCron(final CronDynamicWorkflowOptions options) {

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
                // child workflowでlambdaを呼び出すのでchild workflowに権限付与するための
                // StartWorkflowOptionsを作成
                String defaultLambdaRoleArn = configHelper.getSwfLambdaRoleArn();
                StartWorkflowOptions startWorkflowOptions =
                        new StartWorkflowOptions().withLambdaRole(defaultLambdaRoleArn);

                CronDecorator cronDecorator = new CronDecorator(
                        options.getCronExpression(), expiration, tz, clock);

                // parent workflow's run id set child workflow id
                DecisionContextProvider decisionProvider =
                        new DecisionContextProviderImpl();
                DecisionContext decisionContext = decisionProvider.getDecisionContext();
                GenericWorkflowClient genericWorkflowClient =
                        decisionContext.getWorkflowClient();
                String workflowId = genericWorkflowClient.generateUniqueId();
                WorkflowType workflowType = options.getWorkflowType();

                DynamicWorkflowClient client =
                        cronDecorator.decorate(DynamicWorkflowClient.class,
                                new DynamicWorkflowClientImpl(
                                        new WorkflowExecution()
                                                .withWorkflowId(workflowId),
                                        workflowType, startWorkflowOptions));

                // RuntimeExceptionが発生したときに5秒,10秒,20秒で5回リトライする
                //                long initialRetryIntervalSeconds = 5;
                //                int maximumAttempts = 5;
                //                List<Class<? extends Throwable>> exceptionsToRetry = new ArrayList<>();
                //                exceptionsToRetry.add(RuntimeException.class);
                //                ExponentialRetryPolicy retryPolicy =
                //                        new ExponentialRetryPolicy(initialRetryIntervalSeconds)
                //                                .withMaximumAttempts(maximumAttempts)
                //                                .withExceptionsToRetry(exceptionsToRetry);
                //                Decorator retryDecorator = new RetryDecorator(retryPolicy);
                //                DynamicWorkflowClient retryDecoratedClient =
                //                        retryDecorator.decorate(DynamicWorkflowClient.class, client);

                Promise<String> wait =
                        client.startWorkflowExecution(options.getWorkflowArguments(),
                                startWorkflowOptions, String.class);

                // cron発火したときに1回〜4回呼ばれてしまうのでsleepで暫定回避
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                selfClient.startCron(options, startWorkflowOptions, wait);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                e.printStackTrace();
            }

            @Override
            protected void doFinally() throws Throwable {
                if (clock.isReplaying()) {
                    System.out.println("Replaying");
                }
            }
        };
    }

    @Asynchronous
    private void terminateWorkflow(Promise<Void> waitFor) {
        System.out.println("terminate workflow");
        startCronTask.cancel(null);
    }
}