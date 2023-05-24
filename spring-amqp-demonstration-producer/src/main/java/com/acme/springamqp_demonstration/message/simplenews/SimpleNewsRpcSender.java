package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news-rpc.properties")
@Service
public class SimpleNewsRpcSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcSender.class);

  private final String simpleNewsRoutingKey;
  private final String simpleNewsExchangeNameRcp;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SimpleNewsRpcSender(
      RabbitTemplate rabbitTemplate,
      @Value("${simple.news.rpc.routing.key}") String simpleNewsRoutingKey,
      @Value("${simple.news.rpc.exchange.name}") String simpleNewsExchangeNameRcp
  ) {
    this.rabbitTemplate = rabbitTemplate;
    this.simpleNewsRoutingKey = simpleNewsRoutingKey;
    this.simpleNewsExchangeNameRcp = simpleNewsExchangeNameRcp;
  }

  @Scheduled(cron = "${simple.news.rpc.sender.cron}")
  private void reportCurrentTime() {
    String message = "Simple News RCP " + currentDateTimeProvider.getCurrentDateTime();
    sendSimpleNewsRpc(message);
  }

  String sendSimpleNewsRpc(String message) {
    LOGGER.info("Sending following message RCP : {}", message);
    String receivedMessage =
        (String) rabbitTemplate.convertSendAndReceive(simpleNewsExchangeNameRcp, simpleNewsRoutingKey, message);
    LOGGER.info("Received following message RCP : {}", receivedMessage);
    return receivedMessage;
  }

}
