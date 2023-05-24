package com.acme.springamqp_demonstration.message.specialmessages;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:special-messages.properties")
@Configuration
public class SpecialMessagesConfig {

  private static final boolean NON_DURABLE = false;
  private static final boolean AUTO_DELETE = false;

  @Value("${special.messages.exchange.name1}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_1;

  @Value("${special.messages.queue.any1}")
  private String SPECIAL_MESSAGES_QUEUE_ANY_1;

  @Value("${special.messages.queue.all}")
  private String SPECIAL_MESSAGES_QUEUE_ALL;

  @Bean
  public Declarables specialMessage1Bindings() {
    HeadersExchange specialMessagesExchange1 = new HeadersExchange(SPECIAL_MESSAGES_EXCHANGE_NAME_1, NON_DURABLE, AUTO_DELETE);
    Queue simpleNewsQueue1 = new Queue(SPECIAL_MESSAGES_QUEUE_ANY_1, NON_DURABLE);
    Queue simpleNewsQueue2 = new Queue(SPECIAL_MESSAGES_QUEUE_ALL, NON_DURABLE);
    return new Declarables(
        specialMessagesExchange1,
        simpleNewsQueue1,
        simpleNewsQueue2,
        BindingBuilder.bind(simpleNewsQueue1).to(specialMessagesExchange1).whereAny(SpecialMessageBindingRules.ruleAny).match(),
        BindingBuilder.bind(simpleNewsQueue2).to(specialMessagesExchange1).whereAll(SpecialMessageBindingRules.ruleAll).match());
  }

}
