package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultTaskStartToCloseTimeoutSeconds = 30, defaultExecutionStartToCloseTimeoutSeconds = 60)
public interface HelloLambdaWorkflow {

    @Execute(name = "HelloLambdaWorkflow", version = "1.0.0")
    void hello(String name) throws Exception;

}
