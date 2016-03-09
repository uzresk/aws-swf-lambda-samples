package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.dynamic_wf;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.interceptors.Decorator;
import com.amazonaws.services.simpleworkflow.flow.interceptors.ExponentialRetryPolicy;
import com.amazonaws.services.simpleworkflow.flow.interceptors.RetryDecorator;
import com.amazonaws.services.simpleworkflow.flow.worker.LambdaFunctionClient;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class DynamicChildWorkflowImpl implements DynamicChildWorkflow {

    private static Logger logger = Logger.getLogger(DynamicChildWorkflowImpl.class);

    @Override
    public void helloLambda() throws Exception {

        new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                ConfigHelper configHelper = ConfigHelper.createConfig();
                DecisionContextProvider decisionProvider =
                        new DecisionContextProviderImpl();

                DecisionContext decisionContext = decisionProvider.getDecisionContext();
                LambdaFunctionClient lambdaClient =
                        decisionContext.getLambdaFunctionClient();

                // Retry
                long initialRetryIntervalSeconds = 1;
                int maximumAttempts = 5;
                List<Class<? extends Throwable>> exceptionsToRetry = new ArrayList<>();
                exceptionsToRetry.add(RuntimeException.class);
                ExponentialRetryPolicy retryPolicy =
                        new ExponentialRetryPolicy(initialRetryIntervalSeconds)
                                .withMaximumAttempts(maximumAttempts)
                                .withExceptionsToRetry(exceptionsToRetry);
                Decorator retryDecorator = new RetryDecorator(retryPolicy);
                LambdaFunctionClient decoratedLambdaClient =
                        retryDecorator.decorate(LambdaFunctionClient.class, lambdaClient);

                String now = "\"" + LocalDateTime.now().toString() + "\"";
                Promise<String> val = decoratedLambdaClient.scheduleLambdaFunction(
                        configHelper.getSwfLambdaFunction(), now, 30);
                processResult(val);
            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
               logger.error(e); 
               throw e;
            }
            @Override
            protected void doFinally() throws Throwable {
                // noop
            }

        };
    }

    @Asynchronous
    private void processResult(Promise<String> lambdaClientResult) {
        System.out.println("ready:" + LocalTime.now() + "-" + lambdaClientResult.get());
    }
}