package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.CurrentDateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news-rpc-json.properties")
@Service
public class SimpleNewsRpcJsonSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcJsonSender.class);

  private final String simpleNewsRoutingKey;
  private final String simpleNewsExchangeNameRcpJson;

  private final RabbitTemplate jsonRabbitTemplate;

  private final CurrentDateTimeProvider currentDateTimeProvider = new CurrentDateTimeProvider();

  public SimpleNewsRpcJsonSender(
      RabbitTemplate jsonRabbitTemplate,
      @Value("${simple.news.rpc.json.routing.key}") String simpleNewsRoutingKey,
      @Value("${simple.news.rpc.json.exchange.name}") String simpleNewsExchangeNameRcpJson
  ) {
    this.jsonRabbitTemplate = jsonRabbitTemplate;
    this.simpleNewsRoutingKey = simpleNewsRoutingKey;
    this.simpleNewsExchangeNameRcpJson = simpleNewsExchangeNameRcpJson;
  }

  @Scheduled(cron = "${simple.news.rpc.json.sender.cron}")
  private void reportCurrentTime() {
    sendSimpleNewsRpcJson(
        new SimpleNews("Simple News RCP JSON", currentDateTimeProvider.getCurrentDateTime())
    );
  }

  SimpleNews sendSimpleNewsRpcJson(SimpleNews simpleNews) {
    LOGGER.info("Sending following message RCP as JSON : {}", simpleNews);
    SimpleNews receivedSimpleNews = jsonRabbitTemplate.convertSendAndReceiveAsType(
        simpleNewsExchangeNameRcpJson,
        simpleNewsRoutingKey,
        simpleNews,
        ParameterizedTypeReference.forType(SimpleNews.class)
    );
    LOGGER.info("Received following message RCP as JSON : {}", receivedSimpleNews);
    return receivedSimpleNews;
  }

}
