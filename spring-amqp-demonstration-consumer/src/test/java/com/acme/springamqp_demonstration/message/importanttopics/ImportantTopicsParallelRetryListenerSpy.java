package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class ImportantTopicsParallelRetryListenerSpy {

  @Autowired
  RabbitTemplate rabbitTemplate;

  @Bean
  ImportantTopicsParallelRetryListener importantTopicsParallelRetryListener() {
    return spy(new ImportantTopicsParallelRetryListener(rabbitTemplate));
  }

}
