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

public class SpecialMessagesForErrorSenderTest {

  private static final String SPECIAL_MESSAGE_EXCHANGE_NAME = "com.acme.special-messages.exchange2";
  private static final String SPECIAL_MESSAGE__ROUTING_KEY = "";
  private SpecialMessagesForErrorSender specialMessagesForErrorSender;
  private RabbitTemplate rabbitTemplateMock;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @BeforeEach
  public void setUp() {
    rabbitTemplateMock = Mockito.mock(RabbitTemplate.class);
    specialMessagesForErrorSender =
        new SpecialMessagesForErrorSender(rabbitTemplateMock, SPECIAL_MESSAGE_EXCHANGE_NAME, SPECIAL_MESSAGE__ROUTING_KEY);
  }

  @Test
  public void testSendSimpleNews() {
    String salesValue = "sales";
    int pricingModelValue_1 = 1;
    String messageContent = "Special Message ... ";
    MessageProperties messageProperties = new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties();
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());
    Message message = new Jackson2JsonMessageConverter().toMessage(specialMessage, messageProperties);

    assertThatCode(() -> this.specialMessagesForErrorSender.sendSpecialMessages(message)).doesNotThrowAnyException();
    Mockito.verify(this.rabbitTemplateMock)
        .convertAndSend(eq(SPECIAL_MESSAGE_EXCHANGE_NAME), eq(SPECIAL_MESSAGE__ROUTING_KEY), eq(message));
  }
}
