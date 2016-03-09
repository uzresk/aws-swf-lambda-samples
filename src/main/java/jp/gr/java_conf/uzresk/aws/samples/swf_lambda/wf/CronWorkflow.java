package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 600, defaultTaskStartToCloseTimeoutSeconds = 10)
public interface CronWorkflow {

    /**
     * Start workflow that executes activity according to options.
     */
    @Execute(name = "CronLambdaWorkflow", version = "1.0.2")
    void startCron(CronWorkflowOptions options);

}