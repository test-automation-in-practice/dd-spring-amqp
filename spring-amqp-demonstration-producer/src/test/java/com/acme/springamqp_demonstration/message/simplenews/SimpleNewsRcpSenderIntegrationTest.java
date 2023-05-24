package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
@Testcontainers
public class SimpleNewsRcpSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRcpSenderIntegrationTest.class);

  @Autowired
  private SimpleNewsRpcSender simpleNewsRpcSender;

  @Test
  public void testSendSimpleNewsRpc() {
    String receivedMessage = simpleNewsRpcSender.sendSimpleNewsRpc("Simple News ...");
    assertTrue(receivedMessage.startsWith("Send back incoming Simple News RCP"));
  }

  @RabbitListener(queues = { "${simple.news.rpc.queue.name}" })
  public String receiveSimpleNewsRcp(String message) {
    LOGGER.info("Received Simple News RCP: " + message);
    return "Send back incoming Simple News RCP - " + message;
  }

}
