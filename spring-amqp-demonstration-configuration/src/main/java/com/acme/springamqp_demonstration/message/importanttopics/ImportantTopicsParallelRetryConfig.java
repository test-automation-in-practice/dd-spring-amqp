package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:important-topics.properties")
@Configuration
public class ImportantTopicsParallelRetryConfig {

  private static final boolean NON_DURABLE = false;
  private static final boolean AUTO_DELETE = false;

  @Value("${important.topics.routing.key.pr}")
  private String IMPORTANT_TOPICS_ROUTING_KEY_PR;

  @Value("${important.topics.exchange.name.pr}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

  @Value("${important.topics.queue.name.general.pr}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR;

  @Value("${important.topics.exchange.name.pr.dlx}")
  private String IMPORTANT_TOPICS_EXCHANGE_PR_DLX;

  @Value("${important.topics.queue.name.general.pr.dlq}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ;

  @Bean
  public FanoutExchange importantTopicsExchangeGeneralPrDlx() {
    return ExchangeBuilder.fanoutExchange(IMPORTANT_TOPICS_EXCHANGE_PR_DLX).durable(false).build();
  }

  @Bean
  public Queue importantTopicsQueueNameGeneralPrDlq() {
    return QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR_DLQ).build();
  }

  @Bean
  public Binding importantTopicsBindingGeneralPrDl() {
    return BindingBuilder.bind(importantTopicsQueueNameGeneralPrDlq()).to(importantTopicsExchangeGeneralPrDlx());
  }


  @Bean
  public Declarables importantTopicsBindingsPr() {
    TopicExchange importantTopicExchangePr = new TopicExchange(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, NON_DURABLE, AUTO_DELETE);
    Queue importantTopicsGeneralQueuePr = QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_PR).build();
    return new Declarables(
        importantTopicExchangePr,
        importantTopicsGeneralQueuePr,
        BindingBuilder.bind(importantTopicsGeneralQueuePr).to(importantTopicExchangePr).with(IMPORTANT_TOPICS_ROUTING_KEY_PR)
    );
  }
  
}
