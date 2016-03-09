package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple.retry;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultTaskStartToCloseTimeoutSeconds = 60, defaultExecutionStartToCloseTimeoutSeconds = 180)
public interface HelloLambdaRetryWorkflow {

    @Execute(name = "HelloLambdaRetryWorkflow", version = "1.0.0")
    void hello(String name) throws Exception;

}
