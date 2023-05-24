package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:important-topics.properties")
@Service
public class ImportantTopicsSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsSender.class);

  private final RabbitTemplate rabbitTemplate;
  private final String importantTopicsExchangeName;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public ImportantTopicsSender(
      RabbitTemplate rabbitTemplate,
      @Value("${important.topics.exchange.name1}") String importantTopicsExchangeName) {
    this.rabbitTemplate = rabbitTemplate;
    this.importantTopicsExchangeName = importantTopicsExchangeName;
  }

  @Scheduled(cron = "${important.topics.sender1.cron}")
  private void reportCurrentTime() {
    String currentDateTime = currentDateTimeProvider.getCurrentDateTime();
    String message = "Important Topics " + currentDateTime;
    LOGGER.info("Sending following Important Topics: {}", message);
    sendImportantTopics("com.acme.general", message.concat(" general"));
    sendImportantTopics("com.acme.general.sport", message.concat(" general.sport"));
    sendImportantTopics("com.acme.important-topics.lifestyle", message.concat(" important-topics.lifestyle"));
    sendImportantTopics("com.acme.important-topics.sport.football", message.concat(" important-topics.sport.football"));

    message = "Important Topics - general 1";
    LOGGER.info("Sending following Important Topics {} and current date time {}", message, currentDateTime);
    sendImportantTopicsObjects("com.acme.general", new ImportantTopic(message, currentDateTime));
    sendImportantTopicsObjects("com.acme.general.sport", new ImportantTopic(message, currentDateTime));
  }

  void sendImportantTopics(String routingKey, String message) {
    LOGGER.info("Sending following Important Topics '{}' with routing Key {}", message, routingKey);
    rabbitTemplate.convertAndSend(importantTopicsExchangeName, routingKey, message);
  }

  void sendImportantTopicsObjects(String routingKey, ImportantTopic importantTopic) {
    LOGGER.info("Sending following Important Topics {} and current date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
    rabbitTemplate.convertAndSend(importantTopicsExchangeName, routingKey, importantTopic);
  }

}
