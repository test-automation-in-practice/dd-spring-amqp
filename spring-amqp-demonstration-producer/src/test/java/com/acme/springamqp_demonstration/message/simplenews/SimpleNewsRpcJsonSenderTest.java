package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.ParameterizedTypeReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;

public class SimpleNewsRpcJsonSenderTest {

  private static final String SIMPLE_NEWS_RCP_JSON_EXCHANGE_NAME = "com.acme.simple-news-rpc-json.exchange";
  private static final String SIMPLE_NEWS_RCP_JSON_ROUTING_KEY = "";
  private SimpleNewsRpcJsonSender simpleNewsRpcJsonSender;
  private RabbitTemplate rabbitTemplateMock;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    simpleNewsRpcJsonSender =
        new SimpleNewsRpcJsonSender(
            rabbitTemplateMock,
            SIMPLE_NEWS_RCP_JSON_EXCHANGE_NAME,
            SIMPLE_NEWS_RCP_JSON_ROUTING_KEY
        );
  }

  @Test
  public void testSimpleNewsRpcJsonSender() {
    assertThatCode(() -> this.simpleNewsRpcJsonSender.sendSimpleNewsRpcJson(
        new SimpleNews("Simple News RCP JSON", currentDateTimeProvider.getCurrentDateTime())
    )).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertSendAndReceiveAsType(
            eq(SIMPLE_NEWS_RCP_JSON_ROUTING_KEY),
            eq(SIMPLE_NEWS_RCP_JSON_EXCHANGE_NAME),
            isA(SimpleNews.class),
            eq(ParameterizedTypeReference.forType(SimpleNews.class))
        );
  }
}
