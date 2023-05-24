package com.acme.springamqp_demonstration.message.simplenews;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class,
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        DefaultContainerFactoryConfig.class, // mandatory for testing :/
        SimpleNewsRpcJsonConfig.class,
        SimpleNewsRpcJsonListenerSpy.class // loads the listeners as spy.
    }
)
public class SimpleNewsRpcJsonListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcJsonListenerIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS = 1;

  @Autowired
  private RabbitTemplate jsonRabbitTemplate;

  @Value("${simple.news.rpc.json.exchange.name}")
  private String SIMPLE_NEWS_RPC_JSON_EXCHANGE_NAME;

  private final SimpleNewsRpcJsonListener simpleNewsRpcJsonListener;

  SimpleNewsRpcJsonListenerIntegrationTcTest(
      @Autowired SimpleNewsRpcJsonListener simpleNewsRpcJsonListener
  ) {
    this.simpleNewsRpcJsonListener = simpleNewsRpcJsonListener;
  }

  @Test
  public void testParallelRetryListeners() {
    String message = "Simple News RPC JSON ...";
    String currentDateTime = "16.05.2023 17:17:00 CET";
    LOGGER.info("Sending following Simple News RPC JSON : {}", message);
    String routingKey = "";
    SimpleNews simpleNews = new SimpleNews(message, currentDateTime);
    SimpleNews receivedSimpleNews =
        jsonRabbitTemplate.convertSendAndReceiveAsType(
            SIMPLE_NEWS_RPC_JSON_EXCHANGE_NAME,
            routingKey,
            simpleNews,
            ParameterizedTypeReference.forType(SimpleNews.class));

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(simpleNewsRpcJsonListener, times(WANTED_NUMBER_OF_INVOCATIONS)).receiveSimpleNewsRcpJson(any());
      assertNotNull(receivedSimpleNews);
      assertTrue(receivedSimpleNews.messageContent().startsWith("Sending back incoming Simple News RCP JSON"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

