package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.ParallelRetryQueuesInterceptor;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:important-topics.properties")
public class ImportantTopicsParallelRetryListener {

  RabbitTemplate rabbitTemplate;

  public ImportantTopicsParallelRetryListener(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsParallelRetryListener.class);

  @RabbitListener(
      queues = {"${important.topics.queue.name.general.pr}"},
      containerFactory = "retryQueuesContainerFactory",
      ackMode = "MANUAL")
  public void receiveGeneralTopics(final ImportantTopic importantTopic) throws Exception {
    LOGGER.info("Received Important Topics with topic general: {} with date time {}", importantTopic.messageContent(), importantTopic.currentDateTime());
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
    LOGGER.info("Receiving Important Topics with topic general {} failed!", importantTopic.currentDateTime());
    MessageProperties props = message.getMessageProperties();
    rabbitTemplate.convertAndSend(
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_EXCHANGE),
        props.getHeader(ParallelRetryQueuesInterceptor.HEADER_X_ORIGINAL_ROUTING_KEY),
        message);
  }

}
