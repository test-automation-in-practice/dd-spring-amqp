package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;

public class ImportantTopicsForParallelRetrySenderTest {

  private static final String IMPORTANT_TOPIC_PR_EXCHANGE_NAME = "com.acme.important-topics.exchange.pr";
  private ImportantTopicsParallelRetrySender importantTopicsParallelRetrySender;
  private RabbitTemplate rabbitTemplateMock;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    importantTopicsParallelRetrySender =
        new ImportantTopicsParallelRetrySender(rabbitTemplateMock, IMPORTANT_TOPIC_PR_EXCHANGE_NAME);
  }

  @Test
  public void testSendImportantTopicsObjects() {
    String routingKey = "com.acme.general";
    String message = "breaking news error pr";
    ImportantTopic importantTopic = new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime());
    assertThatCode(() -> this.importantTopicsParallelRetrySender.sendImportantTopicsObjects(routingKey, importantTopic))
        .doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(IMPORTANT_TOPIC_PR_EXCHANGE_NAME), eq(routingKey), eq(importantTopic));
  }
}
