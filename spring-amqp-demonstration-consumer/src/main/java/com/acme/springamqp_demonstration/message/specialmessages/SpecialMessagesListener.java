package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@PropertySource("classpath:special-messages.properties")
@Service
public class SpecialMessagesListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesListener.class);

  @RabbitListener(
      queues = {"${special.messages.queue.any1}"},
      messageConverter = "jackson2Converter",
      containerFactory = "defaultContainerFactory")
  public void receiveSpecialMessageAny1(@Payload SpecialMessage specialMessage, @Header("from") String from, @Header("pricingModel") String pricingModel) {
    LOGGER.info("**** Received Special Message 1 props {} and {} ", specialMessage.messageContent(), specialMessage.currentDateTime());
    LOGGER.info("**** Received Special Message 1 header {} and {} ", from, pricingModel);
  }

  @RabbitListener(
      queues = {"${special.messages.queue.all}"},
      messageConverter = "jackson2Converter",
      containerFactory = "defaultContainerFactory")
  public void receiveSpecialMessageAll1(@Payload SpecialMessage specialMessage) {
    LOGGER.info("**** Received Special Message 2 props {} and {} ", specialMessage.messageContent(), specialMessage.currentDateTime());
  }

}
