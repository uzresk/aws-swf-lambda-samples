package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple.retry;

import java.time.LocalTime;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;
import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple.HelloLambdaWorkflowClientExternal;
import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple.HelloLambdaWorkflowClientExternalFactory;
import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.simple.HelloLambdaWorkflowClientExternalFactoryImpl;

public class HelloLambdaRetryWorkflowExecutionStarter {

    public static void main(String[] args) throws Exception {

        ConfigHelper configHelper = ConfigHelper.createConfig();
        AmazonSimpleWorkflow swfService = configHelper.createSWFClient();
        String domain = configHelper.getDomain();
        String defaultLambdaRoleArn = configHelper.getSwfLambdaRoleArn();

        HelloLambdaWorkflowClientExternalFactory clientFactory =
                new HelloLambdaWorkflowClientExternalFactoryImpl(swfService, domain);
        HelloLambdaWorkflowClientExternal workflow = clientFactory.getClient();

        // give the ARN of an IAM role that allows SWF to invoke lambda functions on your behalf
        StartWorkflowOptions options =
                new StartWorkflowOptions().withLambdaRole(defaultLambdaRoleArn);

        // Start Workflow Execution
        workflow.hello(LocalTime.now().toString(), options);

        // WorkflowExecution is available after workflow creation 
        WorkflowExecution workflowExecution = workflow.getWorkflowExecution();
        System.out.println("Started helloLambda workflow with workflowId=\""
                + workflowExecution.getWorkflowId() + "\" and runId=\""
                + workflowExecution.getRunId() + "\"");
    }

}
