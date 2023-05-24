package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class SimpleNewsRpcListenerSpy {

  @Bean
  SimpleNewsRpcListener simpleNewsRpcListener() {
    return spy(new SimpleNewsRpcListener());
  }
}
