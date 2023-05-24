package com.acme.springamqp_demonstration.message.specialmessages;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:special-messages.properties")
@Configuration
public class SpecialMessagesForErrorConfig {

  private static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

  @Value("${special.messages.exchange.name2}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2;

  @Value("${special.messages.queue.any2}")
  private String SPECIAL_MESSAGES_QUEUE_ANY_2;

  @Value("${special.messages.exchange.name2.dlx}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2_DLX;

  @Value("${special.messages.queue.any2.dlq}")
  private String SPECIAL_MESSAGES_QUEUE_ANY_2_DLQ;

  @Value("${special.messages.exchange.name2.plx}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2_PLX;

  @Value("${special.messages.queue.any2.plq}")
  private String SPECIAL_MESSAGES_QUEUE_ANY_2_PLQ;

  @Bean
  public Declarables specialMessage2Bindings() {
    HeadersExchange specialMessagesExchange2 = ExchangeBuilder
        .headersExchange(SPECIAL_MESSAGES_EXCHANGE_NAME_2).durable(false).build();
    Queue simpleNewsQueue2 = QueueBuilder.nonDurable(SPECIAL_MESSAGES_QUEUE_ANY_2)
        .withArgument(X_DEAD_LETTER_EXCHANGE, SPECIAL_MESSAGES_EXCHANGE_NAME_2_DLX).build();

    FanoutExchange specialMessagesExchange2Dlx = ExchangeBuilder
        .fanoutExchange(SPECIAL_MESSAGES_EXCHANGE_NAME_2_DLX).durable(false).build();
    Queue specialMessageQueue2Dlq = QueueBuilder.nonDurable(SPECIAL_MESSAGES_QUEUE_ANY_2_DLQ).build();

    FanoutExchange specialMessagesExchange2Plx = ExchangeBuilder
        .fanoutExchange(SPECIAL_MESSAGES_EXCHANGE_NAME_2_PLX).durable(false).build();
    Queue specialMessageQueue2Plq = QueueBuilder.nonDurable(SPECIAL_MESSAGES_QUEUE_ANY_2_PLQ).build();

    return new Declarables(
        specialMessagesExchange2,
        simpleNewsQueue2,
        BindingBuilder.bind(simpleNewsQueue2).to(specialMessagesExchange2)
            .whereAny(SpecialMessageBindingRules.ruleAny).match(),
        specialMessagesExchange2Dlx,
        specialMessageQueue2Dlq,
        BindingBuilder.bind(specialMessageQueue2Dlq).to(specialMessagesExchange2Dlx),
        specialMessagesExchange2Plx,
        specialMessageQueue2Plq,
        BindingBuilder.bind(specialMessageQueue2Plq).to(specialMessagesExchange2Plx)
    );
  }

}
