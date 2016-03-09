package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

public class CronWorkflowOptions {

    //    private ActivityType activity;
    //
    //    private Object[] activityArguments;

    private String cronExpression;

    private String timeZone;
    
    private int continueAsNewAfterSeconds;

    //    public ActivityType getActivity() {
    //        return activity;
    //    }
    //
    //    public void setActivity(ActivityType activity) {
    //        this.activity = activity;
    //    }
    //
    //    public Object[] getActivityArguments() {
    //        return activityArguments;
    //    }
    //
    //    public void setActivityArguments(Object[] activityArguments) {
    //        this.activityArguments = activityArguments;
    //    }

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