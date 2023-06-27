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
        SimpleNewsConfig.class,
        SimpleNewsListenerSpy.class, // loads the listeners as spy.
        SimpleNewsWorkersTestListener.class
    }
)
public class SimpleNewsListenerIntegrationTcTest extends RabbitMqTestContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleNewsListenerIntegrationTcTest.class);
  public static final int WANTED_NUMBER_OF_INVOCATIONS_Q2 = 2;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_C1 = 1;
  public static final int WANTED_NUMBER_OF_INVOCATIONS_C2 = 1;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${simple.news.exchange.name}")
  private String SIMPLE_NEWS_EXCHANGE_NAME;

  private final SimpleNewsListener simpleNewsListener;

  private final SimpleNewsWorker receiveSimpleNews1Consumer1;

  private final SimpleNewsWorker receiveSimpleNews1Consumer2;

  SimpleNewsListenerIntegrationTcTest(
      @Autowired SimpleNewsListener simpleNewsListener,
      @Autowired SimpleNewsWorker receiveSimpleNews1Consumer1,
      @Autowired SimpleNewsWorker receiveSimpleNews1Consumer2
  ) {
    this.simpleNewsListener = simpleNewsListener;
    this.receiveSimpleNews1Consumer1 = receiveSimpleNews1Consumer1;
    this.receiveSimpleNews1Consumer2 = receiveSimpleNews1Consumer2;
  }

  @Test
  public void testParallelRetryListeners() {
    String message = "Simple News ...";
    LOGGER.info("Sending following Simple News: {}", message);
    String routingKey = "";
    rabbitTemplate.convertAndSend(SIMPLE_NEWS_EXCHANGE_NAME, routingKey, message);
    rabbitTemplate.convertAndSend(SIMPLE_NEWS_EXCHANGE_NAME, routingKey, message);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    try {
      verify(simpleNewsListener, times(WANTED_NUMBER_OF_INVOCATIONS_Q2)).receiveSimpleNews2(any());
      verify(receiveSimpleNews1Consumer1, times(WANTED_NUMBER_OF_INVOCATIONS_C1)).receiveMsg(any());
      verify(receiveSimpleNews1Consumer2, times(WANTED_NUMBER_OF_INVOCATIONS_C2)).receiveMsg(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

