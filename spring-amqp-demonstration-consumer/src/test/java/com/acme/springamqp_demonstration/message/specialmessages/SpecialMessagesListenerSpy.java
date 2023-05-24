package com.acme.springamqp_demonstration.message.specialmessages;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class SpecialMessagesListenerSpy {

  @Bean
  SpecialMessagesListener specialMessagesListener() {
    return spy(new SpecialMessagesListener());
  }
}
