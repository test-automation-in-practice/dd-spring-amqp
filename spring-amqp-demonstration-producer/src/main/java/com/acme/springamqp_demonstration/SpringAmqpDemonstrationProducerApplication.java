package com.acme.springamqp_demonstration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringAmqpDemonstrationProducerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringAmqpDemonstrationProducerApplication.class, args);
  }

}
