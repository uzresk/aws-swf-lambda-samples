package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.dynamic_wf;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 10)
public interface DynamicChildWorkflow {

    /**
     * Start workflow that executes activity according to options.
     */
    @Execute(name = "DynamicChildWorkflow", version = "1.0.1")
    void helloLambda() throws Exception;

}