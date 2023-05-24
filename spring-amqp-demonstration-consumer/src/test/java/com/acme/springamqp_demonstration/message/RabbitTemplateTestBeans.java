package com.acme.springamqp_demonstration.message;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

@TestConfiguration
public class RabbitTemplateTestBeans {

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

  int getPort() {
    try {
      return Integer.parseInt(String.valueOf(System.getProperty("spring.rabbitmq.port")));
    } catch (NumberFormatException e) {
      return 5672;
    }
  }

}
