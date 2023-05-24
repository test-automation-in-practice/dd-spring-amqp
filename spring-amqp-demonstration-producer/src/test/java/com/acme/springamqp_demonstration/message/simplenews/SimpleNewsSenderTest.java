package com.acme.springamqp_demonstration.message.simplenews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

public class SimpleNewsSenderTest {

  private static final String SIMPLE_NEWS_EXCHANGE_NAME = "com.acme.simple-news.exchange";
  private static final String SIMPLE_NEWS_ROUTING_KEY = "";
  private SimpleNewsSender simpleNewsSender;
  private RabbitTemplate rabbitTemplateMock;

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    simpleNewsSender = new SimpleNewsSender(rabbitTemplateMock, SIMPLE_NEWS_EXCHANGE_NAME, SIMPLE_NEWS_ROUTING_KEY);
  }

  @Test
  public void testSendSimpleNews() {
    assertThatCode(() -> this.simpleNewsSender.sendSimpleNews("Simple News ...")).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(SIMPLE_NEWS_ROUTING_KEY), eq(SIMPLE_NEWS_EXCHANGE_NAME), startsWith("Simple News "));
  }

}
