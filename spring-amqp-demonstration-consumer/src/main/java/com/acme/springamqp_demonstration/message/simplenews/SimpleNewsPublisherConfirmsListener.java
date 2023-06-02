package com.acme.springamqp_demonstration.message.simplenews;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@PropertySource("classpath:simple-news-pub-con.properties")
@Service
public class SimpleNewsPublisherConfirmsListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsPublisherConfirmsListener.class);

  @RabbitListener(
      queues = { "${simple.news.queue.pub-con.name}" },
      containerFactory = "defaultContainerFactory",
      ackMode = "MANUAL"
  )
  public void receiveSimpleNewsPublisherConfirms(
      String message,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
      ) {
    LOGGER.info("Received simple news Publisher Confirms: " + message);
    try {
      channel.basicAck(deliveryTag, false);
      LOGGER.info("Setting manual acknowledgement!");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
