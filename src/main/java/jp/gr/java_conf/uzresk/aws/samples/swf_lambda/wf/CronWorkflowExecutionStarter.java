package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecutionAlreadyStartedException;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class CronWorkflowExecutionStarter {

    private static AmazonSimpleWorkflow swfService;

    private static String domain;

    public static void main(String[] args) throws Exception {

        String cronPattern = "0/30 * * * * *";
        String timeZone = "Asia/Tokyo";
        int continueAsNewAfterSeconds = 0;
        try {
            continueAsNewAfterSeconds = 60*60*23;
        } catch (NumberFormatException e) {
            System.err.println("Value of CONTINUE_AS_NEW_AFTER_SECONDS is not int: " + "0");
            System.exit(1);
        }
        // Load configuration
        ConfigHelper configHelper = ConfigHelper.createConfig();

        // Create the client for Simple Workflow Service
        swfService = configHelper.createSWFClient();
        domain = configHelper.getDomain();

        // Start Workflow execution
        CronWorkflowClientExternalFactory clientFactory = new CronWorkflowClientExternalFactoryImpl(swfService, domain);

        // Use Activity + cronPattern as instance id to ensure that only one workflow per pattern for a given activity is active at a time.
        CronWorkflowClientExternal workflow = clientFactory.getClient("swf-lambda:" + cronPattern);
        
        try {
            CronWorkflowOptions cronOptions = new CronWorkflowOptions();
            cronOptions.setContinueAsNewAfterSeconds(continueAsNewAfterSeconds);
            cronOptions.setTimeZone(timeZone);
            cronOptions.setCronExpression(cronPattern);

            // give the ARN of an IAM role that allows SWF to invoke lambda functions on your behalf
//            String defaultLambdaRoleArn = configHelper.getSwfLambdaRoleArn();
//            StartWorkflowOptions options = new StartWorkflowOptions().withLambdaRole(defaultLambdaRoleArn);
            workflow.startCron(cronOptions);

            // WorkflowExecution is available after workflow creation 
            WorkflowExecution workflowExecution = workflow.getWorkflowExecution();
            System.out.println("Started Cron workflow with workflowId=\"" + workflowExecution.getWorkflowId() + "\" and runId=\""
                    + workflowExecution.getRunId() + "\" with cron pattern=" + cronPattern);

        } catch (WorkflowExecutionAlreadyStartedException e) {
            // It is expected to get this exception if start is called before workflow run is completed.
            System.out.println("Cron workflow with workflowId=\"" + workflow.getWorkflowExecution().getWorkflowId()
                    + " is already running for the pattern=" + cronPattern);
        }
        System.exit(0);
    }
}
