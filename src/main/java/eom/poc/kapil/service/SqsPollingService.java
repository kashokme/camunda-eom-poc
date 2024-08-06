package eom.poc.kapil.service;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eom.poc.kapil.model.WorkflowVariables;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.amazonaws.services.sqs.AmazonSQSAsync;

@Service
@Slf4j
public class SqsPollingService {

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Autowired
    private ZeebeClient zeebeClient;

    WorkflowVariables workflowVariables = WorkflowVariables.getInstance();

    @Scheduled(fixedDelay = 5000)
    public void pollSqs() {
        if(!workflowVariables.getWaitForJobEnabled()) {
            log.info("Polling disabled... No jobs running.");
            return;
        }

        log.info("Polling SQS for messages");
        String queueUrl = "http://localhost:4566/000000000000/my-test-queue";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(10)
                .withMaxNumberOfMessages(1);

        ReceiveMessageResult result = amazonSQSAsync.receiveMessage(receiveMessageRequest);
        log.info("Received " + result.getMessages().size() + " messages from SQS");

        for (Message message : result.getMessages()) {
            processMessage(message);
            amazonSQSAsync.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
        }
    }

    private void processMessage(Message message) {
        log.info("Received SQS message: " + message.getBody());
        JsonObject jsonObject = JsonParser.parseString(message.getBody()).getAsJsonObject();
        String snsMessage = jsonObject.get("Message").getAsString();
        zeebeClient.newPublishMessageCommand()
            .messageName("Job_Completion_Msg")
            .correlationKey(workflowVariables.getDummyJobId())
            .variables(Map.of("JobResult", snsMessage))
            .send()
            .join();
        log.info("Job Completion sent to Camunda. Disabling polling.");
        workflowVariables.setWaitForJobEnabled(false);
    }
}
