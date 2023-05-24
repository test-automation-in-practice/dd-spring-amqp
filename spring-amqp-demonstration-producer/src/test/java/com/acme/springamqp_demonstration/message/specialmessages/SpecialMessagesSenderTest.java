package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;

public class SpecialMessagesSenderTest {

  private static final String SPECIAL_MESSAGE_EXCHANGE_NAME = "com.acme.special-messages.exchange1";
  private static final String SPECIAL_MESSAGE__ROUTING_KEY = "";
  private SpecialMessagesSender specialMessagesSender;
  private RabbitTemplate rabbitTemplateMock;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    specialMessagesSender = new SpecialMessagesSender(rabbitTemplateMock, SPECIAL_MESSAGE_EXCHANGE_NAME, SPECIAL_MESSAGE__ROUTING_KEY);
  }

  @Test
  public void testSendSimpleNews() {
    String salesValue = "sales";
    int pricingModelValue_1 = 1;
    String messageContent = "Special Message ... ";
    MessageProperties messageProperties = new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties();
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());
    Message message = new Jackson2JsonMessageConverter().toMessage(specialMessage, messageProperties);

    assertThatCode(() -> this.specialMessagesSender.sendSpecialMessages(message)).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(SPECIAL_MESSAGE_EXCHANGE_NAME), eq(SPECIAL_MESSAGE__ROUTING_KEY), eq(message));
  }
}
