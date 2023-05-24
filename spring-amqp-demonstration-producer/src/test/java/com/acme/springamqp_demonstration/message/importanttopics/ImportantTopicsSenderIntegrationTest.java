package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
public class ImportantTopicsSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsSenderIntegrationTest.class);

  @Autowired
  private ImportantTopicsSender importantTopicsSender;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @Autowired
  private ImportantTopicsGeneralListener importantTopicsGeneralListener;

  @Test
  public void testSendImportantTopics() {
    String importantTopicsRoutingKey = "com.acme.important-topics.lifestyle";
    String message = "breaking news";
    importantTopicsSender.sendImportantTopics(importantTopicsRoutingKey, message);

    String generalRoutingKey = "com.acme.general";
    importantTopicsSender.sendImportantTopics(generalRoutingKey, message);
    importantTopicsSender.sendImportantTopics(generalRoutingKey, message);

    importantTopicsSender
        .sendImportantTopicsObjects(
            generalRoutingKey,
            new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime())
        );
    importantTopicsSender
        .sendImportantTopicsObjects(
            generalRoutingKey,
            new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime())
        );
    importantTopicsSender
        .sendImportantTopicsObjects(
            generalRoutingKey,
            new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime())
        );

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> !messageFromQueueImportantTopic.isEmpty(),
            is(true)
        );
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> importantTopicsGeneralListener.getMessageFromQueueGeneral().size() == 2,
            is(true)
        );
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> importantTopicsGeneralListener.getMessageFromQueueGeneralImportantTopic().size() == 3,
            is(true)
        );
  }

  List<String> messageFromQueueImportantTopic = new ArrayList<>();

  @RabbitListener(queues = { "${important.topics.queue.name.important-topics}" })
  public void receiveImportantTopics(String message) {
    messageFromQueueImportantTopic.add(message);
    LOGGER.info("Received Important Topics: " + message);
  }

  @Bean
  public ImportantTopicsGeneralListener receiveImportantTopicsGeneralListener() {
    return new ImportantTopicsGeneralListener();
  }

}
