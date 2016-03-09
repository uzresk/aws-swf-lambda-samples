package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.dynamic_wf;

import com.amazonaws.services.simpleworkflow.model.WorkflowType;

public class CronDynamicWorkflowOptions {

    private WorkflowType workflowType;

    private String workflowId;

    private Object[] workflowArguments;

    private String cronExpression;

    private String timeZone;

    private int continueAsNewAfterSeconds;

    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public Object[] getWorkflowArguments() {
        return workflowArguments;
    }

    public void setWorkflowArguments(Object[] workflowArguments) {
        this.workflowArguments = workflowArguments;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getContinueAsNewAfterSeconds() {
        return continueAsNewAfterSeconds;
    }

    public void setContinueAsNewAfterSeconds(int continueAsNewAfterSeconds) {
        this.continueAsNewAfterSeconds = continueAsNewAfterSeconds;
    }

}