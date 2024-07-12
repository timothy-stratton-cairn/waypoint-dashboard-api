package com.cairn.waypoint.dashboard.utility.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsUtility {

  @Value("${waypoint.dashboard.email-notification-queue-url}")
  private String queueUrl;

  private final AmazonSQS sqsClient;

  private final ObjectMapper mapper = new ObjectMapper();

  public SqsUtility(AmazonSQS sqsClient) {
    this.sqsClient = sqsClient;
  }

  public void sendMessage(Object messageBody) throws JsonProcessingException {
    SendMessageRequest sendMessageRequest = new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageBody(mapper.writeValueAsString(messageBody));

    sqsClient.sendMessage(sendMessageRequest);
  }
}
