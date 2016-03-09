package jp.gr.java_conf.uzresk.aws.samples.swf_lambda.wf;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;

import jp.gr.java_conf.uzresk.aws.samples.swf_lambda.common.ConfigHelper;

public class WorkflowHost {

    public static final String DECISION_TASK_LIST = "taskList1";

    public static void main(String[] args) throws Exception {
        ConfigHelper configHelper = ConfigHelper.createConfig();
        AmazonSimpleWorkflow swfService = configHelper.createSWFClient();
        String domain = configHelper.getDomain();

        final WorkflowWorker worker = new WorkflowWorker(swfService, domain, DECISION_TASK_LIST);
        worker.addWorkflowImplementationType(CronWorkflowImpl.class);
        worker.addWorkflowImplementationType(ChildWorkflowImpl.class);
        worker.setRegisterDomain(true);
        worker.setDomainRetentionPeriodInDays(1);
        worker.start();

        System.out.println("Workflow Host Service Started...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                try {
                    worker.shutdownAndAwaitTermination(1, TimeUnit.MINUTES);
                    System.out.println("Workflow Host Service Terminated...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("Please press any key to terminate service.");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }
}
