package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@ContextConfiguration(classes = MessageConverterBeans.class)
public class SpecialMessagesSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesSenderIntegrationTest.class);

  @Autowired
  private SpecialMessagesSender specialMessagesSender;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @Test
  public void testSendSpecialMessages() {
    String messageContent = "Special Message ";
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());

    String salesValue = "sales";
    String customerValue = "customer";
    int pricingModelValue_1 = 1;
    int pricingModelValue_2 = 2;
    specialMessagesSender.sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties()
        )
    );
    specialMessagesSender.sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(customerValue, pricingModelValue_1).createMessageProperties()
        )
    );
    specialMessagesSender.sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_2).createMessageProperties()
        )
    );
    specialMessagesSender.sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(customerValue, pricingModelValue_2).createMessageProperties()
        )
    );

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueAny.size() == 3,
            is(true)
        );
    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueAll.size() == 1,
            is(true)
        );
  }

  List<SpecialMessage> messageFromQueueAny = new ArrayList<>();
  List<SpecialMessage> messageFromQueueAll = new ArrayList<>();

  @RabbitListener(queues = {"${special.messages.queue.any1}"}, messageConverter = "jackson2Converter")
  public void receiveSpecialMessage1(@Payload SpecialMessage specialMessage, @Header("from") String from, @Header("pricingModel") String pricingModel) {
    messageFromQueueAny.add(specialMessage);
    LOGGER.info("**** Received Special Message 1 props {} and {} ", specialMessage.messageContent(), specialMessage.currentDateTime());
    LOGGER.info("**** Received Special Message 1 header {} and {} ", from, pricingModel);
  }

  @RabbitListener(queues = {"${special.messages.queue.all}"}, messageConverter = "jackson2Converter")
  public void receiveSpecialMessage2(@Payload SpecialMessage specialMessage) {
    messageFromQueueAll.add(specialMessage);
    LOGGER.info("**** Received Special Message 2 props {} and {} ", specialMessage.messageContent(), specialMessage.currentDateTime());
  }

}
