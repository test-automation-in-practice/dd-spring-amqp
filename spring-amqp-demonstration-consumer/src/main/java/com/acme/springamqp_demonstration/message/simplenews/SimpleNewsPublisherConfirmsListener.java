package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news-pub-con.properties")
@Service
public class SimpleNewsPublisherConfirmsListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsPublisherConfirmsListener.class);

  @RabbitListener(
      queues = { "${simple.news.queue.pub-con.name}" },
      containerFactory = "defaultContainerFactory"
  )
  public void receiveSimpleNewsPublisherConfirms(String message) {
    LOGGER.info("Received simple news 1: " + message);
  }

}
