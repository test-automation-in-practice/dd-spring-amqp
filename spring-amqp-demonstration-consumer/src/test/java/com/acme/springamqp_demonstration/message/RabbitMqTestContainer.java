package com.acme.springamqp_demonstration.message;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class RabbitMqTestContainer {

  private static final int RABBITMQ_PORT = 5672;
  @SuppressWarnings("rawtypes")
  static GenericContainer rabbitMQContainer =
      new GenericContainer("rabbitmq:3-management").withExposedPorts(RABBITMQ_PORT, 15672);

  static {
    rabbitMQContainer.start();
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    System.setProperty("spring.rabbitmq.host", rabbitMQContainer.getHost());
    System.setProperty("spring.rabbitmq.port", String.valueOf(rabbitMQContainer.getMappedPort(RABBITMQ_PORT)));
    dynamicPropertyRegistry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
    dynamicPropertyRegistry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(RABBITMQ_PORT));
  }
}
