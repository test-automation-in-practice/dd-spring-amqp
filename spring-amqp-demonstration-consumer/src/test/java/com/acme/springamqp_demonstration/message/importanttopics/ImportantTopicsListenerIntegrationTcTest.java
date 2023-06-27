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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        ImportantTopicsConfig.class, // creates the exchange and the queue if not already created.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        ImportantTopicsListenerSpy.class, // loads the listeners as spy.
        ImportantTopicsGeneralListenerSpy.class // load the special for testing prepared listener. :/ Spying leads to ambiguous method recognition.
    }
)
public class ImportantTopicsListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsListenerIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS_IT = 2;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_GENERAL_STRING = 2;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_GENERAL_OBJECT = 2;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name1}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME;

  private final ImportantTopicsListener importantTopicsListener;
  private final ImportantTopicsGeneralListener importantTopicsGeneralListener;

  ImportantTopicsListenerIntegrationTcTest(
      @Autowired ImportantTopicsListener importantTopicsListener,
      @Autowired ImportantTopicsGeneralListener importantTopicsGeneralListener
  ) {
    this.importantTopicsListener = importantTopicsListener;
    this.importantTopicsGeneralListener = importantTopicsGeneralListener;
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
      verify(importantTopicsGeneralListener, times(WANTED_NUMBER_OF_INVOCATIONS_GENERAL_STRING)).receiveGeneralTopicsString(any());
      verify(importantTopicsGeneralListener, times(WANTED_NUMBER_OF_INVOCATIONS_GENERAL_OBJECT)).receiveGeneralTopics(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

