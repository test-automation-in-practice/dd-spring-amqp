package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news-rpc.properties")
@Service
public class SimpleNewsRpcListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcListener.class);

  @RabbitListener(
      queues = { "${simple.news.rpc.queue.name}" },
      containerFactory = "defaultContainerFactory"
  )
  public String receiveSimpleNewsRcp(String message) {
    LOGGER.info("Received Simple News RCP: " + message);
    return "Send back incoming Simple News RCP - " + message;
  }

}
