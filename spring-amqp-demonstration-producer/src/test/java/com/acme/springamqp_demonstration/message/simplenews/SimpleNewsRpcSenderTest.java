package com.acme.springamqp_demonstration.message.simplenews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

public class SimpleNewsRpcSenderTest {

  private static final String SIMPLE_NEWS_RCP_EXCHANGE_NAME = "com.acme.simple-news-rpc.exchange";
  private static final String SIMPLE_NEWS_RCP_ROUTING_KEY = "";
  private SimpleNewsRpcSender simpleNewsRpcSender;
  private RabbitTemplate rabbitTemplateMock;

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    simpleNewsRpcSender =
        new SimpleNewsRpcSender(rabbitTemplateMock, SIMPLE_NEWS_RCP_EXCHANGE_NAME, SIMPLE_NEWS_RCP_ROUTING_KEY);
  }

  @Test
  public void testSendSimpleNewsRpc() {
    assertThatCode(() -> this.simpleNewsRpcSender.sendSimpleNewsRpc("Simple News RCP ...")).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertSendAndReceive(
            eq(SIMPLE_NEWS_RCP_ROUTING_KEY),
            eq(SIMPLE_NEWS_RCP_EXCHANGE_NAME),
            startsWith("Simple News RCP ")
        );
  }
}
