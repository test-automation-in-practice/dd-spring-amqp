package com.acme.springamqp_demonstration.message.simplenews;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * FIXME - Modify this test after the <a href="https://github.com/spring-projects/spring-amqp/milestone/210">
 *   release of Spring AMQP 3.0.5 on June 19, 2023</a> because of the:
 * <ul>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2456">Bug 2456</a></li>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2457">Fix 2457</a></li>
 * </ul>
 * Use the original implementation ({@link SimpleNewsWorker}) as Beans or use the original listener implementation
 * {@link SimpleNewsWorkersListener}.
 */
@Service
public class SimpleNewsWorkersTestListener {

  @Bean
  public SimpleNewsWorkerTest receiveSimpleNews1Consumer1() {
    return new SimpleNewsWorkerTest(1);
  }

  @Bean
  public SimpleNewsWorkerTest receiveSimpleNews1Consumer2() {
    return new SimpleNewsWorkerTest(2);
  }


}
