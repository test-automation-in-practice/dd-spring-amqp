package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class ImportantTopicsListenerSpy {

  @Bean
  ImportantTopicsListener importantTopicsListener() {
    return spy(new ImportantTopicsListener());
  }
}
