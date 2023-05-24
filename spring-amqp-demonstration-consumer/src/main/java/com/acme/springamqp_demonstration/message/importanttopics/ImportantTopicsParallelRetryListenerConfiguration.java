package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.ParallelRetryQueues;
import com.acme.springamqp_demonstration.message.ParallelRetryQueuesInterceptor;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:important-topics.properties")
@Configuration
@EnableRabbit
public class ImportantTopicsParallelRetryListenerConfiguration {

  @Value("${important.topics.exchange.name.pr.dlx}")
  private String IMPORTANT_TOPICS_EXCHANGE_PR_DLX;

  @Value("${important.topics.queue.name.general.pr.dlq}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ;

  @Value("${important.topics.queue.name.general.pr.dlq.r1}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R1;

  @Value("${important.topics.queue.name.general.pr.dlq.r2}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R2;

  @Value("${important.topics.queue.name.general.pr.dlq.r3}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R3;

  @Bean
  public Queue retryQueue1() {
    return QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R1)
        .deadLetterExchange(IMPORTANT_TOPICS_EXCHANGE_PR_DLX)
        .deadLetterRoutingKey(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ)
        .build();
  }

  @Bean
  public Queue retryQueue2() {
    return QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R2)
        .deadLetterExchange(IMPORTANT_TOPICS_EXCHANGE_PR_DLX)
        .deadLetterRoutingKey(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ)
        .build();
  }

  @Bean
  public Queue retryQueue3() {
    return QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ_R3)
        .deadLetterExchange(IMPORTANT_TOPICS_EXCHANGE_PR_DLX)
        .deadLetterRoutingKey(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ)
        .build();
  }

  @Bean
  public ParallelRetryQueues retryQueues() {
    return new ParallelRetryQueues(1000, 3.0, 10000, retryQueue1(), retryQueue2(), retryQueue3());
  }

  @Bean
  public ParallelRetryQueuesInterceptor retryQueuesInterceptor(RabbitTemplate rabbitTemplate, ParallelRetryQueues retryQueues) {
    return new ParallelRetryQueuesInterceptor(rabbitTemplate, retryQueues);
  }

  @Bean
  public SimpleRabbitListenerContainerFactory retryQueuesContainerFactory(
      ConnectionFactory connectionFactory, ParallelRetryQueuesInterceptor retryQueuesInterceptor) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    Advice[] adviceChain = { retryQueuesInterceptor };
    factory.setAdviceChain(adviceChain);
    return factory;
  }

}
