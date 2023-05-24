package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.spy;

@TestConfiguration
public class SpecialMessagesForErrorListenerSpy {


  @Autowired
  private RabbitTemplateTestBeans rabbitTemplateTestBeans;

  @Bean
  SpecialMessagesForErrorListener specialMessagesForErrorListener() {
    return spy(new SpecialMessagesForErrorListener(rabbitTemplateTestBeans.rabbitTemplate()));
  }
}
