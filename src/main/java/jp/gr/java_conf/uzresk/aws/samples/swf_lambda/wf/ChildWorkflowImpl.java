package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.amazonaws.services.simpleworkflow.flow.DecisionContext;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProvider;
import com.amazonaws.services.simpleworkflow.flow.DecisionContextProviderImpl;
import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;
import com.amazonaws.services.simpleworkflow.flow.worker.LambdaFunctionClient;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class ChildWorkflowImpl implements ChildWorkflow {

    @Override
    public Promise<String> helloLambda() throws Exception {
        ConfigHelper configHelper = ConfigHelper.createConfig();
        DecisionContextProvider decisionProvider = new DecisionContextProviderImpl();

        DecisionContext decisionContext = decisionProvider.getDecisionContext();
        LambdaFunctionClient lambdaClient = decisionContext.getLambdaFunctionClient();

        String now = "\"" + LocalDateTime.now().toString() + "\"";
        Promise<String> val = lambdaClient.scheduleLambdaFunction(
                configHelper.getSwfLambdaFunction(), now, 30);
        processResult(val);
        return val;
    }

    @Asynchronous
    private void processResult(Promise<String> lambdaClientResult) {
        System.out.println("ready:" + LocalTime.now() + "-" + lambdaClientResult.get());
    }
}