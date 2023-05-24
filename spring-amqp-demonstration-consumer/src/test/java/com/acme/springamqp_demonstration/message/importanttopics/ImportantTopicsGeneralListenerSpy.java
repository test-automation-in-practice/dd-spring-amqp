package com.acme.springamqp_demonstration.message.importanttopics;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

/**
 * FIXME - Modify this test after the <a href="https://github.com/spring-projects/spring-amqp/milestone/210">release
 * of Spring AMQP 3.0.5 on June 19, 2023</a> because of the:
 * <ul>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2456">Bug 2456</a></li>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2457">Fix 2457</a></li>
 * </ul>
 * Use the original implementation ({@link ImportantTopicsGeneralListener}).
 */
@TestConfiguration
public class ImportantTopicsGeneralListenerSpy {

  @Bean
  ImportantTopicsGeneralTestListener importantTopicsGeneralListenerSpyInstance() {
    return spy(new ImportantTopicsGeneralTestListener());
  }
}
