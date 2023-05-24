package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@PropertySource("classpath:special-messages.properties")
@Service
public class SpecialMessagesForErrorListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesForErrorListener.class);

  public static final String HEADER_X_RETRIES_COUNT = "x-retries-count";

  public static final int MAX_RETRIES_COUNT = 2;

  private final RabbitTemplate rabbitTemplate;

  public SpecialMessagesForErrorListener(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Value("${special.messages.exchange.name2}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2;

  @Value("${special.messages.exchange.name2.plx}")
  private String SPECIAL_MESSAGES_EXCHANGE_NAME_2_PLX;

  @Value("${special.messages.routing.key}")
  private String SPECIAL_MESSAGES_ROUTING_KEY;

  @RabbitListener(
      queues = {"${special.messages.queue.any2}"},
      messageConverter = "jackson2Converter",
      containerFactory = "customSpeMesRepublishContainerFactory"
  )
  public void receiveSpecialMessageAny2(
      @Payload SpecialMessage specialMessage,
      @Header("from") String from,
      @Header("pricingModel") String pricingModel) {
    LOGGER.info("**** Received Special Message 2 props {} and {} ", specialMessage.messageContent(),
        specialMessage.currentDateTime());
    LOGGER.info("**** Received Special Message 2 header {} and {} ", from, pricingModel);
    throw new RuntimeException();
  }

  @RabbitListener(
      queues = {"${special.messages.queue.any2.dlq}"},
      messageConverter = "jackson2Converter",
      containerFactory = "defaultContainerFactory"
  )
  public void receiveSpecialMessageAnyDl2(
      Message message,
      @Payload SpecialMessage specialMessage
      ) {
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

  @RabbitListener(
      queues = {"${special.messages.queue.any2.plq}"},
      messageConverter = "jackson2Converter",
      containerFactory = "defaultContainerFactory"
  )
  public void receiveSpecialMessageAnyPl2(
      @Payload SpecialMessage specialMessage,
      @Header("from") String from,
      @Header("pricingModel") String pricingModel
  ) {
    LOGGER.info("**** PLQ Received Special Message 2 props {} and {} ", specialMessage.messageContent(),
        specialMessage.currentDateTime());
    LOGGER.info("**** PLQ Received Special Message 2 header '{}' and pricing model '{}'", from, pricingModel);
  }
}
