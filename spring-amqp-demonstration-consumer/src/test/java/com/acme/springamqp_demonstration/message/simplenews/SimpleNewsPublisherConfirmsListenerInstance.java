package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SimpleNewsPublisherConfirmsListenerInstance {

  @Bean
  SimpleNewsPublisherConfirmsListener simpleNewsPublisherConfirmsListener() {
    return new SimpleNewsPublisherConfirmsListener();
  }
}
