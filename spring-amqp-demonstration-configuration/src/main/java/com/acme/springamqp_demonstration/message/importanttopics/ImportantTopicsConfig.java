package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:important-topics.properties")
@Configuration
public class ImportantTopicsConfig {

  private static final boolean NON_DURABLE = false;
  private static final boolean AUTO_DELETE = false;

  @Value("${important.topics.exchange.name1}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_1;

  @Value("${important.topics.routing.key.1}")
  private String IMPORTANT_TOPICS_ROUTING_KEY_1;

  @Value("${important.topics.routing.key.2}")
  private String IMPORTANT_TOPICS_ROUTING_KEY_2;

  @Value("${important.topics.routing.key.3}")
  private String IMPORTANT_TOPICS_ROUTING_KEY_3;

  @Value("${important.topics.queue.name.important-topics}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_IMPORTANT_TOPICS;

  @Value("${important.topics.queue.name.general1}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_1;

  @Bean
  public Declarables importantTopicsBindings1() {
    TopicExchange importantTopicExchange1 = new TopicExchange(IMPORTANT_TOPICS_EXCHANGE_NAME_1, NON_DURABLE, AUTO_DELETE);
    Queue importantTopicsQueue = new Queue(IMPORTANT_TOPICS_QUEUE_NAME_IMPORTANT_TOPICS, NON_DURABLE);
    Queue importantTopicsGeneralQueue1 = new Queue(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_1, NON_DURABLE);

    return new Declarables(
        importantTopicExchange1,
        importantTopicsQueue,
        importantTopicsGeneralQueue1,
        BindingBuilder.bind(importantTopicsQueue).to(importantTopicExchange1).with(IMPORTANT_TOPICS_ROUTING_KEY_1),
        BindingBuilder.bind(importantTopicsQueue).to(importantTopicExchange1).with(IMPORTANT_TOPICS_ROUTING_KEY_2),
        BindingBuilder.bind(importantTopicsGeneralQueue1).to(importantTopicExchange1).with(IMPORTANT_TOPICS_ROUTING_KEY_3));
  }

}
