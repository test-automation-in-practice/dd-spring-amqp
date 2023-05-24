package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.Test;
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
        DefaultContainerFactoryConfig.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        ImportantTopicsParallelRetryConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsParallelRetryListenerConfiguration.class, // loads the logic for the retry queues container factory.
        ImportantTopicsParallelRetryListenerSpy.class // loads the listeners as spy.
    }
)
public class ImportantTopicsParallelRetryListenerIntegrationTcTest extends RabbitMqTestContainer {

  public static final int WANTED_NUMBER_OF_INVOCATIONS = 8;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

  private final ImportantTopicsParallelRetryListener importantTopicsParallelRetryListener;
  ImportantTopicsParallelRetryListenerIntegrationTcTest(
      @Autowired ImportantTopicsParallelRetryListener importantTopicsParallelRetryListener) {
    this.importantTopicsParallelRetryListener = importantTopicsParallelRetryListener;
  }

  @Test
  public void testParallelRetryListeners() {
    int nb = 2;
    for (int i = 1; i <= nb; i++) {
      rabbitTemplate.convertAndSend(
          IMPORTANT_TOPICS_EXCHANGE_NAME_PR,
          "com.acme.general",
          new ImportantTopic(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, String.valueOf(i))
      );
    }
    try {
      Thread.sleep(15000); // 3 tries - 2 retries, first after 5 and then after 10 seconds makes 15 sec.
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    try {
      verify(importantTopicsParallelRetryListener, times(WANTED_NUMBER_OF_INVOCATIONS)).receiveGeneralTopics(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
