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
        SimpleNewsRpcConfig.class,
        SimpleNewsRpcListenerSpy.class // loads the listeners as spy.
    }
)
public class SimpleNewsRpcListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsRpcListenerIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS = 1;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${simple.news.rpc.exchange.name}")
  private String SIMPLE_NEWS_RPC_EXCHANGE_NAME;

  private final SimpleNewsRpcListener simpleNewsRpcListener;

  SimpleNewsRpcListenerIntegrationTcTest(
      @Autowired SimpleNewsRpcListener SimpleNewsRpcListener
  ) {
    this.simpleNewsRpcListener = SimpleNewsRpcListener;
  }

  @Test
  public void testParallelRetryListeners() {
    String message = "Simple News RPC ...";
    LOGGER.info("Sending following Simple News RPC : {}", message);
    String routingKey = "";
    String receivedMessage =
        (String) rabbitTemplate.convertSendAndReceive(SIMPLE_NEWS_RPC_EXCHANGE_NAME, routingKey, message);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(simpleNewsRpcListener, times(WANTED_NUMBER_OF_INVOCATIONS)).receiveSimpleNewsRcp(any());
      assertNotNull(receivedMessage);
      assertTrue(receivedMessage.startsWith("Send back incoming Simple News RCP"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

