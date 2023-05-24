package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
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
public class SimpleNewsRcpJsonSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRcpJsonSenderIntegrationTest.class);

  @Autowired
  private SimpleNewsRpcJsonSender simpleNewsRpcJsonSender;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @Test
  public void testSendSimpleNewsRpcJson() {
    SimpleNews receivedMessage = simpleNewsRpcJsonSender.sendSimpleNewsRpcJson(
        new SimpleNews("Simple News RCP JSON", currentDateTimeProvider.getCurrentDateTime())
    );
    assertTrue(receivedMessage.messageContent().startsWith("Sending back incoming Simple News RCP JSON"));
  }

  @RabbitListener(queues = { "${simple.news.rpc.json.queue.name}" }, messageConverter = "jackson2Converter")
  public SimpleNews receiveSimpleNewsRcpJson(SimpleNews simpleNews) {
    LOGGER.info("Received Simple News RCP JSON: {} ", simpleNews);
    return new SimpleNews(
        "Sending back incoming Simple News RCP JSON - " + simpleNews.messageContent(),
        simpleNews.currentDateTime());
  }

}
