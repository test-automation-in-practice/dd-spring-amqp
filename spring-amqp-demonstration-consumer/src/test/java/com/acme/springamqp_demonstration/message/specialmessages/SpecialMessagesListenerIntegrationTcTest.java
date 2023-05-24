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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        SpecialMessagesConfig.class,
        SpecialMessagesListenerSpy.class // loads the listeners as spy.
    }
)
public class SpecialMessagesListenerIntegrationTcTest extends RabbitMqTestContainer {

  public static final int WANTED_NUMBER_OF_INVOCATIONS_ANY = 3;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_ALL = 1;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${special.messages.exchange.name1}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME;

  private final SpecialMessagesListener specialMessagesListener;

  SpecialMessagesListenerIntegrationTcTest(
      @Autowired SpecialMessagesListener specialMessagesListener
  ) {
    this.specialMessagesListener = specialMessagesListener;
  }

  @Test
  public void testSpecialMessagesListener() {
    final String routingKey = "";
    String messageContent = "Special Message ";
    String currentDateTime = "17.05.2023 11:33:00 CET";
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTime);

    String salesValue = "sales";
    String customerValue = "customer";
    int pricingModelValue_1 = 1;
    int pricingModelValue_2 = 2;

    List<Message> messages = List.of(
        getMessage(specialMessage, salesValue, pricingModelValue_1),
        getMessage(specialMessage, customerValue, pricingModelValue_1),
        getMessage(specialMessage, salesValue, pricingModelValue_2),
        getMessage(specialMessage, customerValue, pricingModelValue_2)
    );
    messages.forEach(
        message -> rabbitTemplate.convertAndSend(SPECIAL_MESSAGES_EXCHANGE_NAME, routingKey, message)
    );

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(specialMessagesListener, times(WANTED_NUMBER_OF_INVOCATIONS_ANY)).receiveSpecialMessageAny1(any(), any(), any());
      verify(specialMessagesListener, times(WANTED_NUMBER_OF_INVOCATIONS_ALL)).receiveSpecialMessageAll1(any());
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

