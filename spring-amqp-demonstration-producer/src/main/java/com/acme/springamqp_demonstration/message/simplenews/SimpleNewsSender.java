package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news.properties")
@Service
public class SimpleNewsSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsSender.class);

  private final String simpleNewsExchangeName;
  private final String simpleNewsRoutingKey;

  private final RabbitTemplate rabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SimpleNewsSender(
      RabbitTemplate rabbitTemplate,
      @Value("${simple.news.routing.key}") String simpleNewsRoutingKey,
      @Value("${simple.news.exchange.name}") String simpleNewsExchangeName
  ) {
    this.rabbitTemplate = rabbitTemplate;
    this.simpleNewsExchangeName = simpleNewsExchangeName;
    this.simpleNewsRoutingKey = simpleNewsRoutingKey;
  }

  @Scheduled(cron = "${simple.news.sender.cron}")
  private void reportCurrentTime() {
    sendSimpleNews("Simple News " + currentDateTimeProvider.getCurrentDateTime());
  }

  void sendSimpleNews(String message) {
    LOGGER.info("Sending following message: {}", message);
    rabbitTemplate.convertAndSend(simpleNewsExchangeName, simpleNewsRoutingKey, message);
  }

}
