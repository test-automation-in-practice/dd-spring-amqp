package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;

public class ImportantTopicsSenderTest {

  private static final String IMPORTANT_TOPIC_EXCHANGE_NAME = "com.acme.important-topics.exchange1";
  private ImportantTopicsSender importantTopicsSender;
  private RabbitTemplate rabbitTemplateMock;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    importantTopicsSender = new ImportantTopicsSender(rabbitTemplateMock, IMPORTANT_TOPIC_EXCHANGE_NAME);
  }

  @Test
  public void testSendImportantTopics() {
    String routingKey = "com.acme.general";
    String message = "breaking news";
    assertThatCode(() -> this.importantTopicsSender.sendImportantTopics(routingKey, message)).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock).convertAndSend(eq(IMPORTANT_TOPIC_EXCHANGE_NAME), eq(routingKey), eq(message));
  }

  @Test
  public void testSendImportantTopicsObjects() {
    String routingKey = "com.acme.general";
    String message = "breaking news";
    ImportantTopic importantTopic = new ImportantTopic(message, currentDateTimeProvider.getCurrentDateTime());
    assertThatCode(() -> this.importantTopicsSender.sendImportantTopicsObjects(routingKey, importantTopic)).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock).convertAndSend(eq(IMPORTANT_TOPIC_EXCHANGE_NAME), eq(routingKey), eq(importantTopic));
  }
}
