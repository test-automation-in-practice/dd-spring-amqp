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
public class SpecialMessagesSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpecialMessagesSender.class);

  private final RabbitTemplate rabbitTemplate;
  private final String specialMessageExchangeName1;
  private final String specialMessagesRoutingKey;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SpecialMessagesSender(
      RabbitTemplate rabbitTemplate,
      @Value("${special.messages.exchange.name1}") String specialMessageExchangeName1,
      @Value("${special.messages.routing.key}") String specialMessagesRoutingKey
  ) {
    this.rabbitTemplate = rabbitTemplate;
    this.specialMessageExchangeName1 = specialMessageExchangeName1;
    this.specialMessagesRoutingKey = specialMessagesRoutingKey;
  }

  @Scheduled(cron = "${special.messages.sender1.cron}")
  private void reportSpecialMessages() {
    String messageContent = "Special Message ";
    SpecialMessage specialMessage = new SpecialMessage(messageContent, currentDateTimeProvider.getCurrentDateTime());

    String salesValue = "sales";
    String customerValue = "customer";
    int pricingModelValue_1 = 1;
    int pricingModelValue_2 = 2;
    sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_1).createMessageProperties()
        )
    );
    sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(customerValue, pricingModelValue_1).createMessageProperties()
        )
    );
    sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(salesValue, pricingModelValue_2).createMessageProperties()
        )
    );
    sendSpecialMessages(
        new Jackson2JsonMessageConverter().toMessage(
            specialMessage,
            new SpecialMessagePropertiesFactory(customerValue, pricingModelValue_2).createMessageProperties()
        )
    );
  }

  void sendSpecialMessages(Message message) {
    LOGGER.info("Sending following Special Message with the header 'from': {} and 'pricingModel': {}.",
        message.getMessageProperties().getHeader("from"),
        message.getMessageProperties().getHeader("pricingModel"));
    rabbitTemplate.convertAndSend(specialMessageExchangeName1, specialMessagesRoutingKey, message);
  }

}
