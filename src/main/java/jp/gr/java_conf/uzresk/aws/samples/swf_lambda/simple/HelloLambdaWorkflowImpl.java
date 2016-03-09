package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple;

import java.time.LocalTime;

import org.apache.log4j.Logger;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.core.TryCatchFinally;
import com.amazonaws.services.simpleworkflow.flow.worker.LambdaFunctionClient;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class HelloLambdaWorkflowImpl implements HelloLambdaWorkflow {

    private static final Logger logger = Logger.getLogger(HelloLambdaWorkflowImpl.class);

    @SuppressWarnings("unused")
    private TryCatchFinally task = null;

    @Override
    public void hello(String name) throws Exception {

        task = new TryCatchFinally() {

            @Override
            protected void doTry() throws Throwable {
                ConfigHelper configHelper = ConfigHelper.createConfig();
                DecisionContextProvider decisionProvider =
                        new DecisionContextProviderImpl();

                DecisionContext decisionContext = decisionProvider.getDecisionContext();
                LambdaFunctionClient lambdaClient =
                        decisionContext.getLambdaFunctionClient();

                // lambdaの引数はjson
                Promise<String> val = lambdaClient.scheduleLambdaFunction(
                        configHelper.getSwfLambdaFunction(), "\"" + name + "\"", 30);
                // lambda functionの呼び出しが終わるまで待つ
                processResult(val);

            }

            @Override
            protected void doCatch(Throwable e) throws Throwable {
                logger.error("catch exception", e);
                // 例外処理
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
        System.out.println("ready[" + LocalTime.now() + "] lambda return value["
                + lambdaClientResult.get() + "]");
    }
}
