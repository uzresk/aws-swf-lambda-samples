package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.model.TerminateWorkflowExecutionRequest;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class CronWorkflowExecutionTerminate {

    private static AmazonSimpleWorkflow swfService;

    public static void main(String[] args) throws Exception {

        // Load configuration
        ConfigHelper configHelper = ConfigHelper.createConfig();

        // Create the client for Simple Workflow Service
        swfService = configHelper.createSWFClient();

        TerminateWorkflowExecutionRequest request =
                new TerminateWorkflowExecutionRequest().withDomain(configHelper.getDomain())
                        .withWorkflowId("swf-lambda0 * * * * *");
        swfService.terminateWorkflowExecution(request);

        System.exit(0);
    }
}
