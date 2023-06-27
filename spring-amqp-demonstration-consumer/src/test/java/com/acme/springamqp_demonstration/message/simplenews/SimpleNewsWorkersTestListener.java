package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import static org.mockito.Mockito.spy;

@Service
public class SimpleNewsWorkersTestListener {

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer1() {
    return spy(new SimpleNewsWorker(1));
  }

  @Bean
  public SimpleNewsWorker receiveSimpleNews1Consumer2() {
    return spy(new SimpleNewsWorker(2));
  }


}
