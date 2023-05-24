package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
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
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        ImportantTopicsContainerFactoryConfig.class,
        ImportantTopicsForErrorConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsGeneralForErrorListenerSpy.class // loads the listeners as spy.
    }
)
public class ImportantTopicsListenerForErrorIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsListenerForErrorIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS = 3;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_DQ = 1;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name2}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME;

  private final ImportantTopicsGeneralForErrorListener importantTopicsGeneralForErrorListener;

  ImportantTopicsListenerForErrorIntegrationTcTest(
      @Autowired ImportantTopicsGeneralForErrorListener importantTopicsGeneralForErrorListener
  ) {
    this.importantTopicsGeneralForErrorListener = importantTopicsGeneralForErrorListener;
  }

  @Test
  public void testParallelRetryListeners() {
    String currentDateTime = "15.05.2023 10:44 CET";
    String message = "Important Topics " + currentDateTime;
    LOGGER.info("Sending following Important Topics: {}", message);
    rabbitTemplate.convertAndSend(IMPORTANT_TOPICS_EXCHANGE_NAME, "com.acme.general", new ImportantTopic(message, currentDateTime));

    try {
      Thread.sleep(18000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(importantTopicsGeneralForErrorListener, times(WANTED_NUMBER_OF_INVOCATIONS)).receiveGeneralTopicsError(any());
      verify(importantTopicsGeneralForErrorListener, times(WANTED_NUMBER_OF_INVOCATIONS_DQ)).receiveGeneralTopicsDlq(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

