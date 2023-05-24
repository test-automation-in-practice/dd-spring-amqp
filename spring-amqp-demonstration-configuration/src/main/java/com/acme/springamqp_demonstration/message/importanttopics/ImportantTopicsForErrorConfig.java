package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
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
public class ImportantTopicsForErrorConfig {

  private static final boolean NON_DURABLE = false;
  private static final boolean AUTO_DELETE = false;
  private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

  @Value("${important.topics.routing.key.3}")
  private String IMPORTANT_TOPICS_ROUTING_KEY_3;

  @Value("${important.topics.exchange.name2}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_2;

  @Value("${important.topics.queue.name.general2}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_2;

  @Value("${important.topics.exchange.name2.dlx}")
  private String IMPORTANT_TOPICS_EXCHANGE_NAME_2_DLX;

  @Value("${important.topics.queue.name.general2.dlq}")
  private String IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_2_DLQ;

  @Bean
  public Declarables importantTopicsBindings2() {
    TopicExchange importantTopicExchange2 = new TopicExchange(IMPORTANT_TOPICS_EXCHANGE_NAME_2, NON_DURABLE, AUTO_DELETE);
    Queue importantTopicsGeneralQueue2 =
        QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_2)
            .withArgument(X_DEAD_LETTER_EXCHANGE, IMPORTANT_TOPICS_EXCHANGE_NAME_2_DLX)
            .build();
    FanoutExchange importantFanoutExchange2Dlx = new FanoutExchange(IMPORTANT_TOPICS_EXCHANGE_NAME_2_DLX, NON_DURABLE, AUTO_DELETE);
    Queue importantTopicsGeneralQueue2Dlq = QueueBuilder.nonDurable(IMPORTANT_TOPICS_QUEUE_NAME_GENERAL_2_DLQ).build();

    return new Declarables(
        importantTopicExchange2,
        importantTopicsGeneralQueue2,
        importantFanoutExchange2Dlx,
        importantTopicsGeneralQueue2Dlq,
        BindingBuilder.bind(importantTopicsGeneralQueue2).to(importantTopicExchange2).with(IMPORTANT_TOPICS_ROUTING_KEY_3),
        BindingBuilder.bind(importantTopicsGeneralQueue2Dlq).to(importantFanoutExchange2Dlx)
    );
  }
}
