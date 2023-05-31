package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@PropertySource("classpath:simple-news-pub-con.properties")
@Service
public class SimpleNewsPublisherConfirmsSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsPublisherConfirmsSender.class);

  private final String simpleNewsPublisherConfirmsExchangeName;
  private final String simpleNewsPublisherConfirmsRoutingKey;

  private final RabbitTemplate confirmingRabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SimpleNewsPublisherConfirmsSender(
      RabbitTemplate confirmingRabbitTemplate,
      @Value("${simple.news.routing.pub-con.key}") String simpleNewsPublisherConfirmsRoutingKey,
      @Value("${simple.news.exchange.pub-con.name}") String simpleNewsPublisherConfirmsExchangeName
  ) {
    this.confirmingRabbitTemplate = confirmingRabbitTemplate;
    this.simpleNewsPublisherConfirmsExchangeName = simpleNewsPublisherConfirmsExchangeName;
    this.simpleNewsPublisherConfirmsRoutingKey = simpleNewsPublisherConfirmsRoutingKey;
  }

  @Scheduled(cron = "${simple.news.sender.pub-con.cron}")
  private void reportCurrentTime() {
    CorrelationData correlationData = new CorrelationData();
    sendSimpleNewsPublisherConfirms("Simple News Publisher Confirms " + currentDateTimeProvider.getCurrentDateTime(), correlationData);
  }

  void sendSimpleNewsPublisherConfirms(String message, CorrelationData correlationData) {
    LOGGER.info("Sending following message: {}", message);
    confirmingRabbitTemplate.convertAndSend(simpleNewsPublisherConfirmsExchangeName, simpleNewsPublisherConfirmsRoutingKey, message, correlationData);


    try {
      LOGGER.info("The message {} was delivered sent in the next ten seconds. Was is successful? {}", message, correlationData.getFuture().get(10, TimeUnit.SECONDS).isAck());
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

}
