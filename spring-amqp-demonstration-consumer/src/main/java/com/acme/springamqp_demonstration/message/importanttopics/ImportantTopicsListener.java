package com.acme.springamqp_demonstration.message.importanttopics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:important-topics.properties")
@Service
public class ImportantTopicsListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsListener.class);

  @RabbitListener(
      queues = {"${important.topics.queue.name.important-topics}"},
      containerFactory = "defaultContainerFactory"
  )
  public void receiveImportantTopics(String message) {
    LOGGER.info("Received Important Topics: " + message);
  }

}
