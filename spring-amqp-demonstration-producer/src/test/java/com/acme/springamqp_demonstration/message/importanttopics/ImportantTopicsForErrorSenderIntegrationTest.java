package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
public class ImportantTopicsForErrorSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsForErrorSenderIntegrationTest.class);

  @Autowired
  private ImportantTopicsForErrorSender importantTopicsForErrorSender;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  /**
   * Sending erroneous 'important topic' by throwing exception in the listener. Retry three times to send the message.
   * <strong>Note</strong>: The retry number is configured in the application.properties in 'test/resources' folder.
   */
  @Test
  @DisplayName(
      "Sending erroneous 'important topic' by throwing exception in the listener. " +
      "Retry three times to send the message.")
  public void testSendImportantTopicsError() {
    String message = "breaking news error ...";
    String generalRoutingKey = "com.acme.general";

    importantTopicsForErrorSender
        .sendImportantTopicsObjects(
            generalRoutingKey,
            new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime())
        );

    await()
        .atMost(25, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueImportantTopic.size() == 3,
            is(true)
        );
    await()
        .atMost(25, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueDlImportantTopic.size() == 1,
            is(true)
        );
  }

  List<ImportantTopic> messageFromQueueImportantTopic = new ArrayList<>();
  List<ImportantTopic> messageFromQueueDlImportantTopic = new ArrayList<>();

  @RabbitListener(queues = { "${important.topics.queue.name.general2}" })
  public void receiveGeneralTopicsError(final ImportantTopic importantTopic) {
    messageFromQueueImportantTopic.add(importantTopic);
    LOGGER.info("Receiving Important Topics with topic general: {} with date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
    throw new RuntimeException();
  }

  @RabbitListener(queues = { "${important.topics.queue.name.general2.dlq}" })
  public void receiveGeneralTopicsErrorDlq(final ImportantTopic importantTopic) {
    messageFromQueueDlImportantTopic.add(importantTopic);
    LOGGER.info("Receiving Important Topics with topic general {} failed!", importantTopic.currentDateTime());
  }


}
