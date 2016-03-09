package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.dynamic_wf;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 600, defaultTaskStartToCloseTimeoutSeconds = 10)
public interface CronDynamicWorkflow {

    @Execute(name = "CronDynamicWorkflow", version = "1.0.1")
    void startCron(CronDynamicWorkflowOptions options);

}