package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:simple-news.properties")
@Configuration
public class SimpleNewsConfig {

  @Value("${simple.news.exchange.name}")
  private String SIMPLE_NEWS_EXCHANGE_NAME;

  @Value("${simple.news.queue.name.1}")
  private String SIMPLE_NEWS_QUEUE_NAME_1;

  @Value("${simple.news.queue.name.2}")
  private String SIMPLE_NEWS_QUEUE_NAME_2;

  @Bean
  public Declarables simpleNewsBindings() {
    FanoutExchange simpleNewsExchange = ExchangeBuilder.fanoutExchange(SIMPLE_NEWS_EXCHANGE_NAME)
        .durable(false).build();
    Queue simpleNewsQueue1 = QueueBuilder.nonDurable(SIMPLE_NEWS_QUEUE_NAME_1).build();
    Queue simpleNewsQueue2 = QueueBuilder.nonDurable(SIMPLE_NEWS_QUEUE_NAME_2).build();
    return new Declarables(
        simpleNewsExchange,
        simpleNewsQueue1,
        simpleNewsQueue2,
        BindingBuilder.bind(simpleNewsQueue1).to(simpleNewsExchange),
        BindingBuilder.bind(simpleNewsQueue2).to(simpleNewsExchange));
  }

}
