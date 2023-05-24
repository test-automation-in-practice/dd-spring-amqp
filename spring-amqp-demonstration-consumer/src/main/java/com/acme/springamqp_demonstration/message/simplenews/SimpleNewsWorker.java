package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:simple-news.properties")
@RabbitListener(
    queues = { "${simple.news.queue.name.1}" },
    containerFactory = "defaultContainerFactory"
)
public class SimpleNewsWorker {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsWorker.class);

  private final int snCo;

  public SimpleNewsWorker(int snCo) {
    this.snCo = snCo;
  }

  @RabbitHandler
  public void receiveMsg(final String message) {
    LOGGER.info("consumer {},  Received Simple News from Queue 1: {}", snCo, message);
  }

}

