package com.acme.springamqp_demonstration.message.specialmessages;

import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@PropertySource("classpath:special-messages.properties")
@Configuration
@EnableRabbit
public class SpecialMessagesContainerFactoryConfig {

  @Value("${special.messages.exchange.name2.dlx}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2_DLX;

  @Bean
  public RetryOperationsInterceptor retryInterceptorRepublishSpecialMessages(RabbitTemplate rabbitTemplate) {
    return RetryInterceptorBuilder.stateless()
        .recoverer(
            new RepublishMessageRecoverer(rabbitTemplate, SPECIAL_MESSAGES_EXCHANGE_NAME_2_DLX)
        )
        .backOffOptions(5000, 2.0, 300000)
        .maxAttempts(3)
        .build();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory customSpeMesRepublishContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    Advice[] adviceChain = { retryInterceptorRepublishSpecialMessages(new RabbitTemplate(connectionFactory)) };
    factory.setAdviceChain(adviceChain);
    return factory;
  }
}
