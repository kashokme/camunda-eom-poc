package eom.poc.kapil.service;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SnsPublisher {

  @Autowired
  private AmazonSNSAsync snsClient;

  private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:000000000000:my-test-topic";

  public void publishMessage(String message) {
    PublishRequest publishRequest = new PublishRequest()
        .withTopicArn(TOPIC_ARN)
        .withMessage(message);
    PublishResult response = snsClient.publish(publishRequest);
    log.info("Message sent with ID: " + response.getMessageId());
  }
}
