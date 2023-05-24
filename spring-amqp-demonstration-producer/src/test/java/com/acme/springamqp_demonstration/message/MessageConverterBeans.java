package com.acme.springamqp_demonstration.message;

import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MessageConverterBeans {

  @Bean
  public SimpleMessageConverter simpleMessageConverter() {
    return new SimpleMessageConverter();
  }
}
