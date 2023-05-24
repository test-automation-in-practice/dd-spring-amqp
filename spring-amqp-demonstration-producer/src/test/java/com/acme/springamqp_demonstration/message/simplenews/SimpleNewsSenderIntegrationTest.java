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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
public class SimpleNewsSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsSenderIntegrationTest.class);

  @Autowired
  private SimpleNewsSender simpleNewsSender;

  @Test
  public void testSendSimpleNews() {
    simpleNewsSender.sendSimpleNews("Simple News ...");
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> !messageFromQueue1.isEmpty() && !messageFromQueue2.isEmpty(),
            is(true)
        );
  }

  List<String> messageFromQueue1 = new ArrayList<>();
  List<String> messageFromQueue2 = new ArrayList<>();

  @RabbitListener(queues = { "${simple.news.queue.name.1}" }, messageConverter = "simpleMessageConverter")
  public void receiveSimpleNews1(String message) {
    messageFromQueue1.add(message);
    LOGGER.info("Received simple news 1: " + message);
  }

  @RabbitListener(queues = { "${simple.news.queue.name.2}" }, messageConverter = "simpleMessageConverter")
  public void receiveSimpleNews2(String message) {
    messageFromQueue2.add(message);
    LOGGER.info("Received simple news 2: " + message);
  }

}
