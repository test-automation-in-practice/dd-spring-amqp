package com.acme.springamqp_demonstration.message.specialmessages;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import com.acme.springamqp_demonstration.message.specialmessages.model.SpecialMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:special-messages.properties")
@Service
public class SpecialMessagesForErrorSender {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesForErrorSender.class);

  private final String specialMessageExchangeName2;
  private final String specialMessagesRoutingKey;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SpecialMessagesForErrorSender(
      RabbitTemplate rabbitTemplate,
      @Value("${special.messages.exchange.name2}") String specialMessageExchangeName2,
      @Value("${special.messages.routing.key}") String specialMessagesRoutingKey
  ) {
    this.rabbitTemplate = rabbitTemplate;
    this.specialMessageExchangeName2 = specialMessageExchangeName2;
    this.specialMessagesRoutingKey = specialMessagesRoutingKey;
  }

  @Scheduled(cron = "${special.messages.sender2.cron}")
  private void reportSpecialMessages() {
    String messageContent = "Special Message ";
    SpecialMessage specialMessage =
        new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());

    String salesValue = "sales";
    int pricingModelValue_1 = 1;
    sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
        new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties()
        )
    );
  }

  void sendSpecialMessages(Message message) {
    LOGGER.info("Sending following Special Message with the header 'from': {} and 'pricingModel': {}.",
        message.getMessageProperties().getHeader("from"),
        message.getMessageProperties().getHeader("pricingModel"));
    rabbitTemplate.convertAndSend(specialMessageExchangeName2, specialMessagesRoutingKey, message);
  }

}
