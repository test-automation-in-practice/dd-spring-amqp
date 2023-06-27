package com.acme.springamqp_demonstration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("A RabbitMQ application should be running in background, therefor it is disabled")
@SpringBootTest
class SpringAmqpDemonstrationConsumerApplicationTests {

  @Test
  void contextLoads() {
  }

}
