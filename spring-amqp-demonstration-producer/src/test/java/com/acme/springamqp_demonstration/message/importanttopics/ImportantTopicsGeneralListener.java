package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@PropertySource("classpath:important-topics.properties")
@RabbitListener(queues = { "${important.topics.queue.name.general1}" })
public class ImportantTopicsGeneralListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsGeneralListener.class);

  private final List<ImportantTopic> messageFromQueueGeneralImportantTopic = new ArrayList<>();
  private final List<String> messageFromQueueGeneral = new ArrayList<>();

  @RabbitHandler
  public void receiveGeneralTopics(String message) {
    messageFromQueueGeneral.add(message);
    LOGGER.info("Received Important Topics with topic general: " + message);
    LOGGER.info(String.valueOf(messageFromQueueGeneral.size()));
  }

  @RabbitHandler
  public void receiveGeneralTopics(final ImportantTopic importantTopic) {
    messageFromQueueGeneralImportantTopic.add(importantTopic);
    LOGGER.info("Received Important Topics with topic general: {} with date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
    LOGGER.info(String.valueOf(messageFromQueueGeneralImportantTopic.size()));
  }

  public List<ImportantTopic> getMessageFromQueueGeneralImportantTopic() {
    return messageFromQueueGeneralImportantTopic;
  }

  public List<String> getMessageFromQueueGeneral() {
    return messageFromQueueGeneral;
  }
}
