package com.acme.springamqp_demonstration.message;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@PropertySource("classpath:important-topics.properties")
@Configuration
@EnableRabbit
public class DefaultContainerFactoryConfig {

  @Bean
  public RetryOperationsInterceptor retryInterceptor() {
    return RetryInterceptorBuilder.stateless()
        .backOffOptions(5000, 2.0, 300000)
        .maxAttempts(3)
        .build();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory defaultRetryContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    Advice[] adviceChain = { retryInterceptor() };
    factory.setAdviceChain(adviceChain);
    return factory;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory defaultContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    return factory;
  }

}
