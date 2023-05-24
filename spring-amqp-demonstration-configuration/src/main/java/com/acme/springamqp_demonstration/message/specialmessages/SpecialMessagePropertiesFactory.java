package com.acme.springamqp_demonstration.message.specialmessages;

import org.springframework.amqp.core.MessageProperties;

final class SpecialMessagePropertiesFactory {
  private final String fromValue;
  private final int pricingModelValue;

  SpecialMessagePropertiesFactory(String fromValue, int pricingModelValue) {
    this.fromValue = fromValue;
    this.pricingModelValue = pricingModelValue;
  }

  MessageProperties createMessageProperties() {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setHeader(SpecialMessageProperties.HEADER_KEY_FROM, fromValue);
    messageProperties.setHeader(SpecialMessageProperties.HEADER_KEY_PRICING_MODEL, pricingModelValue);
    return messageProperties;
  }
}
