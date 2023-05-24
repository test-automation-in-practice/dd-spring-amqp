package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.ParallelRetryQueuesInterceptor;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;


@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        DefaultContainerFactoryConfig.class,
        ImportantTopicsParallelRetryConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsParallelRetryListenerConfiguration.class, // loads the logic for the retry queues container factory.
        RabbitTemplateTestBeans.class // creates rabbitTemplate instance for auto writing.
    }
)
@Disabled
public class RetryQueuesContainerFactoryIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetryQueuesContainerFactoryIntegrationTcTest.class);

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

  @Disabled
  @Test
  public void testRetryQueuesContainerFactory() {
    int nb = 2;
    for (int i = 1; i <= nb; i++) {
      rabbitTemplate.convertAndSend(
          IMPORTANT_TOPICS_EXCHANGE_NAME_PR,
          "com.acme.general",
          new ImportantTopic(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, String.valueOf(i))
      );
    }
    await()
        .atMost(30, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueue.size() == 8,
            is(true)
        );
  }

  List<ImportantTopic> messageFromQueue = new ArrayList<>();

  @RabbitListener(
      queues = {"${important.topics.queue.name.general.pr}"},
      containerFactory = "retryQueuesContainerFactory",
      ackMode = "MANUAL")
  public void receiveGeneralTopics(final ImportantTopic importantTopic) throws Exception {
    LOGGER.info("Received general topics: {} with date time {}", importantTopic.messageContent(), importantTopic.currentDateTime());
    messageFromQueue.add(importantTopic);
    throw new Exception("This is a very evil exception!");
  }

  @RabbitListener(
      queues = {"${important.topics.queue.name.general.pr.dlq}"},
      containerFactory = "defaultContainerFactory",
      messageConverter = "simpleMessageConverter")
  public void receiveGeneralTopicsDlq(
      Message message,
      @Payload ImportantTopic importantTopic
  ) {
    LOGGER.info("Queue com.acme.general.queue.pr {} failed!", importantTopic.currentDateTime());
    MessageProperties props = message.getMessageProperties();
    rabbitTemplate.convertAndSend(
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_EXCHANGE),
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_ROUTING_KEY),
        message);
  }

}
