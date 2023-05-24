package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        SpecialMessagesForErrorConfig.class,
        SpecialMessagesContainerFactoryConfig.class,
        SpecialMessagesForErrorListenerSpy.class // loads the listeners as spy.
    }
)
public class SpecialMessagesForErrorListenerIntegrationTcTest extends RabbitMqTestContainer {

  public static final int WANTED_NUMBER_OF_INVOCATIONS_ANY = 9;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_ANY_DL = 3;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_ANY_PL = 1;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${special.messages.exchange.name2}")
  private String SPECIAL_MESSAGES_FOR_ERROR_EXCHANGE_NAME;

  private final SpecialMessagesForErrorListener specialMessagesForErrorListener;

  SpecialMessagesForErrorListenerIntegrationTcTest(
      @Autowired SpecialMessagesForErrorListener specialMessagesForErrorListener
  ) {
    this.specialMessagesForErrorListener = specialMessagesForErrorListener;
  }

  @Test
  public void testSpecialMessagesListener() {
    final String routingKey = "";
    String messageContent = "Special Message ";
    String currentDateTime = "17.05.2023 11:33:00 CET";
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTime);

    String salesValue = "sales";
    int pricingModelValue_1 = 1;

    rabbitTemplate.convertAndSend(
        SPECIAL_MESSAGES_FOR_ERROR_EXCHANGE_NAME,
        routingKey,
        getMessage(specialMessage, salesValue, pricingModelValue_1)
    );

    try {
      Thread.sleep(50000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(specialMessagesForErrorListener, times(WANTED_NUMBER_OF_INVOCATIONS_ANY)).receiveSpecialMessageAny2(any(), any(), any());
      verify(specialMessagesForErrorListener, times(WANTED_NUMBER_OF_INVOCATIONS_ANY_DL)).receiveSpecialMessageAnyDl2(any(), any());
      verify(specialMessagesForErrorListener, times(WANTED_NUMBER_OF_INVOCATIONS_ANY_PL)).receiveSpecialMessageAnyPl2(any(), any(), any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  private static Message getMessage(SpecialMessage specialMessage, String fromValue, int pricingModelValue) {
    return new Jackson2JsonMessageConverter().toMessage(
        specialMessage,
        new SpecialMessagePropertiesFactory(fromValue, pricingModelValue).createMessageProperties()
    );
  }

}
