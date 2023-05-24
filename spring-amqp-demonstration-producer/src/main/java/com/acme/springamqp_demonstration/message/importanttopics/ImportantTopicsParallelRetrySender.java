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
public class ImportantTopicsParallelRetrySender {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsParallelRetrySender.class);

  private final RabbitTemplate rabbitTemplate;
  private final String importantTopicsExchangeNamePr;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public ImportantTopicsParallelRetrySender(
      RabbitTemplate rabbitTemplate,
      @Value("${important.topics.exchange.name.pr}") String importantTopicsExchangeNamePr) {
    this.rabbitTemplate = rabbitTemplate;
    this.importantTopicsExchangeNamePr = importantTopicsExchangeNamePr;
  }

  @Scheduled(cron = "${important.topics.sender.pr.cron}")
  private void reportCurrentTime() {
    String routingKey = "com.acme.general";
    String messageContent = "Important Topics - general 2 - error pr - ";
    sendImportantTopicsObjects(
        routingKey,
        new ImportantTopic(messageContent, currentDateTimeProvider.getCurrentDateTime())
    );
  }

  void sendImportantTopicsObjects(
      String routingKey,
      ImportantTopic importantTopic
  ) {
    LOGGER.info("Sending following Important Topics {} and current date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
    rabbitTemplate.convertAndSend(importantTopicsExchangeNamePr, routingKey, importantTopic);
  }

}
