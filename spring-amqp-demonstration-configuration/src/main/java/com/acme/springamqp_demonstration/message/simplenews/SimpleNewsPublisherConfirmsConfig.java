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

@PropertySource("classpath:simple-news-pub-con.properties")
@Configuration
public class SimpleNewsPublisherConfirmsConfig {

  @Value("${simple.news.exchange.pub-con.name}")
  private String SIMPLE_NEWS_PUB_CON_EXCHANGE_NAME;

  @Value("${simple.news.queue.pub-con.name}")
  private String SIMPLE_NEWS_PUB_CON_QUEUE_NAME;

  @Bean
  public Declarables simpleNewsPublisherConfirmsBindings() {
    FanoutExchange simpleNewsPublisherConfirmsExchange =
        ExchangeBuilder.fanoutExchange(SIMPLE_NEWS_PUB_CON_EXCHANGE_NAME)
            .durable(false).build();
    Queue simpleNewsPublisherConfirmsQueue = QueueBuilder.nonDurable(SIMPLE_NEWS_PUB_CON_QUEUE_NAME).build();

    return new Declarables(
        simpleNewsPublisherConfirmsExchange,
        simpleNewsPublisherConfirmsQueue,
        BindingBuilder.bind(simpleNewsPublisherConfirmsQueue).to(simpleNewsPublisherConfirmsExchange));
  }

}
