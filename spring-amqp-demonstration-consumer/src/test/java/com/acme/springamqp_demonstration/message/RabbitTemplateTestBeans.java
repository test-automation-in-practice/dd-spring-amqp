package com.acme.springamqp_demonstration.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@TestConfiguration
public class RabbitTemplateTestBeans {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitTemplateTestBeans.class);

  @Autowired
  MessageConverterBeans messageConverterBeans;

  @Bean
  public RabbitAdmin rabbitAdmin() {
    return new RabbitAdmin(connectionFactory());
  }

  @Bean
  public RabbitTemplate rabbitTemplate() {
    return new RabbitTemplate(connectionFactory());
  }

  @Bean
  public RabbitTemplate jsonRabbitTemplate() {
    RabbitTemplate template = new RabbitTemplate(connectionFactory());
    template.setMessageConverter(messageConverterBeans.jackson2Converter());
    return template;
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    return new CachingConnectionFactory(
        StringUtils.defaultIfEmpty("localhost", System.getProperty("spring.rabbitmq.host")),
        getPort());
  }

  @Bean
  public CachingConnectionFactory confirmingCachingConnectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(getHost(), getPort());
    cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
    return cachingConnectionFactory;
  }

  @Bean
  public RabbitTemplate confirmingRabbitTemplate(CachingConnectionFactory confirmingCachingConnectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(confirmingCachingConnectionFactory);
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
      LOGGER.info("Correlation: {} ack: {}", correlationData, ack);
    });
    return rabbitTemplate;
  }

  String getHost() {
    String host = System.getProperty("spring.rabbitmq.host");
    return Objects.nonNull(host) && !host.isEmpty() ? host : "localhost";
  }

  int getPort() {
    try {
      return Integer.parseInt(String.valueOf(System.getProperty("spring.rabbitmq.port")));
    } catch (NumberFormatException e) {
      return 5672;
    }
  }

}
