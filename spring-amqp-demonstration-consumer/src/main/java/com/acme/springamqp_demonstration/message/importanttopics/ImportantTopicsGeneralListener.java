package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


@Service
@PropertySource("classpath:important-topics.properties")
@RabbitListener(
    id = "importantTopicsGeneralMultiMethodListener",
    queues= {"${important.topics.queue.name.general1}"},
    containerFactory = "defaultContainerFactory"
)
public class ImportantTopicsGeneralListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsGeneralListener.class);

  @RabbitHandler
  public void receiveGeneralTopicsString(String message) {
    LOGGER.info("Received Important Topics with topic general: " + message);
  }

  @RabbitHandler
  public void receiveGeneralTopics(final ImportantTopic importantTopic) {
    LOGGER.info("Received Important Topics with topic general: {} with date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
  }

}
