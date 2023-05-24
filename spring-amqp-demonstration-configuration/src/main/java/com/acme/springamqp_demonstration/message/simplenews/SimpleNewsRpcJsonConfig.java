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

@PropertySource("classpath:simple-news-rpc-json.properties")
@Configuration
public class SimpleNewsRpcJsonConfig {

  @Value("${simple.news.rpc.json.exchange.name}")
  private String SIMPLE_NEWS_EXCHANGE_NAME_RCP_JSON;

  @Value("${simple.news.rpc.json.queue.name}")
  private String SIMPLE_NEWS_QUEUE_NAME_RCP_JSON;

  @Bean
  public Declarables simpleNewsBindingsRpcJson() {
    FanoutExchange simpleNewsExchangeRpc = ExchangeBuilder.fanoutExchange(SIMPLE_NEWS_EXCHANGE_NAME_RCP_JSON)
        .durable(false).build();
    Queue simpleNewsQueueRcp = QueueBuilder.nonDurable(SIMPLE_NEWS_QUEUE_NAME_RCP_JSON).build();
    return new Declarables(
        simpleNewsExchangeRpc,
        simpleNewsQueueRcp,
        BindingBuilder.bind(simpleNewsQueueRcp).to(simpleNewsExchangeRpc)
    );
  }

}
