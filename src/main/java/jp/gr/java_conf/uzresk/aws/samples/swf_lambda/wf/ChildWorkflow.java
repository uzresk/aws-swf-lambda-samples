package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 300, defaultTaskStartToCloseTimeoutSeconds = 10)
public interface ChildWorkflow {

    /**
     * Start workflow that executes activity according to options.
     */
    @Execute(name = "ChildWorkflow", version = "1.0.0")
    Promise<String> helloLambda() throws Exception;

}