package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
public class SimpleNewsPublisherConfirmsSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsPublisherConfirmsSenderIntegrationTest.class);

  @Autowired
  private SimpleNewsPublisherConfirmsSender simpleNewsPublisherConfirmsSender;

  @Test
  public void testSendSimpleNewsPublisherConfirms() {
    CorrelationData correlationData = new CorrelationData();
    simpleNewsPublisherConfirmsSender.sendSimpleNewsPublisherConfirms("Simple News ...", correlationData);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> correlationData.getFuture().get(10, TimeUnit.SECONDS).isAck(),
            is(true)
        );
  }

  @RabbitListener(queues = { "${simple.news.queue.pub-con.name}" }, messageConverter = "simpleMessageConverter")
  public void receiveSimpleNews1(String message) {
    LOGGER.info("Received simple news 1: " + message);
  }


}
