package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SpecialMessagesForErrorSenderIntegrationTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesForErrorSenderIntegrationTest.class);

  @Autowired
  private SpecialMessagesForErrorSender specialMessagesForErrorSender;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Test
  public void testSendSimpleNews() {
    String messageContent = "Special Message ";
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());

    String salesValue = "sales";
    int pricingModelValue_1 = 1;
    specialMessagesForErrorSender.sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties()
        )
    );

    await()
        .atMost(100, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueAny.size() == 9,
            is(true)
        );
    await()
        .atMost(100, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueueDlAny.size() == 3,
            is(true)
        );
    await()
        .atMost(100, TimeUnit.SECONDS)
        .until(
            () -> messageFromQueuePlAny.size() == 1,
            is(true)
        );
  }

  List<SpecialMessage> messageFromQueueAny = new ArrayList<>();
  List<SpecialMessage> messageFromQueueDlAny = new ArrayList<>();
  List<SpecialMessage> messageFromQueuePlAny = new ArrayList<>();

  @RabbitListener(queues = {"${special.messages.queue.any2}"}, messageConverter = "jackson2Converter")
  public void receiveSpecialMessage2(
      @Payload SpecialMessage specialMessage,
      @Header("from") String from,
      @Header("pricingModel") String pricingModel) {
    messageFromQueueAny.add(specialMessage);
    LOGGER.info("**** Received Special Message 2 props {} and {} ", specialMessage.messageContent(),
        specialMessage.currentDateTime());
    LOGGER.info("**** Received Special Message 2 header {} and {} ", from, pricingModel);
    throw new RuntimeException();
  }


  public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";

  public static final int MAX_RETRIES_COUNT = 2;

  @Value("${special.messages.exchange.name2}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2;

  @Value("${special.messages.exchange.name2.plx}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2_PLX;

  @Value("${special.messages.routing.key}")
  private String SPECIAL_MESSAGES_ROUTING_KEY;


  @RabbitListener(queues = {"${special.messages.queue.any2.dlq}"}, messageConverter = "jackson2Converter")
  public void receiveSpecialMessageDl2(
      Message message,
      @Payload SpecialMessage specialMessage
  ) {
    messageFromQueueDlAny.add(specialMessage);
    LOGGER.info("**** DLQ Received Special Message 2 props {} and {} ", specialMessage.messageContent(),
        specialMessage.currentDateTime());
    LOGGER.info("**** DLQ Received Special Message 2 header'{}' and pricing model '{}'",
        message.getMessageProperties().getHeaders().get(SpecialMessageProperties.HEADER_KEY_FROM),
        message.getMessageProperties().getHeaders().get(SpecialMessageProperties.HEADER_KEY_PRICING_MODEL));

    Integer retriesCnt = (Integer) message.getMessageProperties().getHeaders().get(HEADER_X_RETRIES_COUNT);
    if (retriesCnt == null)
      retriesCnt = 1;
    if (retriesCnt > MAX_RETRIES_COUNT) {
      LOGGER.info("Sending message to the parking lot queue");
      rabbitTemplate.convertAndSend(SPECIAL_MESSAGES_EXCHANGE_NAME_2_PLX, SPECIAL_MESSAGES_ROUTING_KEY, message);
      return;
    }
    LOGGER.info("Retrying message for the {} time", retriesCnt);
    message.getMessageProperties().getHeaders().put(HEADER_X_RETRIES_COUNT, ++retriesCnt);
    rabbitTemplate.convertAndSend(SPECIAL_MESSAGES_EXCHANGE_NAME_2, SPECIAL_MESSAGES_ROUTING_KEY, message);
  }

  @RabbitListener(queues = {"${special.messages.queue.any2.plq}"}, messageConverter = "jackson2Converter")
  public void processParkingLotQueue(
      @Payload SpecialMessage specialMessage,
      @Header("from") String from,
      @Header("pricingModel") String pricingModel
  ) {
    messageFromQueuePlAny.add(specialMessage);
    LOGGER.info("**** PLQ Received Special Message 2 props {} and {} ", specialMessage.messageContent(),
        specialMessage.currentDateTime());
    LOGGER.info("**** PLQ Received Special Message 2 header '{}' and pricing model '{}'", from, pricingModel);
  }

}
