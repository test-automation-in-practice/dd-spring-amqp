package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * FIXME - Modify this test after the <a href="https://github.com/spring-projects/spring-amqp/milestone/210">release
 * of Spring AMQP 3.0.5 on June 19, 2023</a> because of the:
 * <ul>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2456">Bug 2456</a></li>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2457">Fix 2457</a></li>
 * </ul>
 * Modify the part of {@link ImportantTopicsGeneralTestListener} by replacing it with the verify command on the original
 * implementation {@link ImportantTopicsGeneralListener}.
 *
 */
@Disabled
@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        ImportantTopicsConfig.class, // creates the exchange and the queue if not already created.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        ImportantTopicsListenerSpy.class, // loads the listeners as spy.
        ImportantTopicsGeneralTestListener.class // load the special for testing prepared listener. :/ Spying leads to ambiguous method recognition.
    }
)
public class ImportantTopicsListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsListenerIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS_IT = 2;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name1}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME;

  private final ImportantTopicsListener importantTopicsListener;

  ImportantTopicsListenerIntegrationTcTest(
      @Autowired ImportantTopicsListener importantTopicsListener
  ) {
    this.importantTopicsListener = importantTopicsListener;
  }

  @Test
  public void testParallelRetryListeners() {
    String currentDateTime = "15.05.2023 10:44 CET";
    String message = "Important Topics " + currentDateTime;
    LOGGER.info("Sending following Important Topics: {}", message);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME, "com.acme.general", message.concat(" general"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME,"com.acme.general.sport", message.concat(" general.sport"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME,"com.acme.important-topics.lifestyle", message.concat(" important-topics.lifestyle"));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME,"com.acme.important-topics.sport.football", message.concat(" important-topics.sport.football"));

    message = "Important Topics - general 1";
    LOGGER.info("Sending following Important Topics {} and current date time {}", message, currentDateTime);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME,"com.acme.general", new ImportantTopic(message, currentDateTime));
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME,"com.acme.general.sport", new ImportantTopic(message, currentDateTime));


    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(importantTopicsListener, times(WANTED_NUMBER_OF_INVOCATIONS_IT)).receiveImportantTopics(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    // Workaround for the "ImportantTopicsGeneralListener".
    // Almost same implementation but with array so gathering the received messages.
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () ->
                ImportantTopicsGeneralTestListener.RECEIVED_IMPORTANT_TOPIC.size() == 2
                    && ImportantTopicsGeneralTestListener.RECEIVED_MESSAGES.size() == 2,
            is(true)
        );
  }

}

