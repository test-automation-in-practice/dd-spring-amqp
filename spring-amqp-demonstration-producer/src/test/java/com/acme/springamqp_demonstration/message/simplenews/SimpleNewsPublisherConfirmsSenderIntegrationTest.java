package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
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

  @RabbitListener(
      queues = {"${simple.news.queue.pub-con.name}"},
      messageConverter = "simpleMessageConverter",
      ackMode = "MANUAL"
  )
  public void receiveSimpleNewsPublisherConfirms(
      String payload,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
  ) {
    LOGGER.info("Received simple news Publisher Confirms: " + payload);
    try {
      channel.basicAck(deliveryTag, false);
      LOGGER.info("Setting manual acknowledgement!");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
