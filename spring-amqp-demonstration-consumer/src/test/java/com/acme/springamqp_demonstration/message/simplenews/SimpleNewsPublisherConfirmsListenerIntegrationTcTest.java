package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        SimpleNewsPublisherConfirmsConfig.class,
        SimpleNewsPublisherConfirmsListenerInstance.class
    }
)
public class SimpleNewsPublisherConfirmsListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsPublisherConfirmsListenerIntegrationTcTest.class);

  @Autowired
  private RabbitTemplate confirmingRabbitTemplate;

  @Value("${simple.news.exchange.pub-con.name}")
  private String SIMPLE_NEWS_PUBLISHER_CONFIRMS_EXCHANGE_NAME;

  @Test
  public void testParallelRetryListeners() {
    String message = "Simple News Publisher Confirms ...";
    LOGGER.info("Sending following Simple News Publisher Confirms: {}", message);
    String routingKey = "";
    CorrelationData correlationData = new CorrelationData();
    confirmingRabbitTemplate
        .convertAndSend(SIMPLE_NEWS_PUBLISHER_CONFIRMS_EXCHANGE_NAME, routingKey, message, correlationData);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> correlationData.getFuture().get(10, TimeUnit.SECONDS).isAck(),
            is(true)
        );
  }

}

