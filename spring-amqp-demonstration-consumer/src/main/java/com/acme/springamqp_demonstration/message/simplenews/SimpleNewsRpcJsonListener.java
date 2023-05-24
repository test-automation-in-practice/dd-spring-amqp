package com.acme.springamqp_demonstration.message.simplenews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:simple-news-rpc-json.properties")
@Service
public class SimpleNewsRpcJsonListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcJsonListener.class);

  @RabbitListener(
      queues = { "${simple.news.rpc.json.queue.name}" },
      messageConverter = "jackson2Converter",
      containerFactory = "defaultContainerFactory"
  )
  public SimpleNews receiveSimpleNewsRcpJson(SimpleNews simpleNews) {
    LOGGER.info("Received Simple News RCP JSON: {} ", simpleNews);
    return new SimpleNews(
        "Sending back incoming Simple News RCP JSON - " + simpleNews.messageContent(),
        simpleNews.currentDateTime());
  }

}
