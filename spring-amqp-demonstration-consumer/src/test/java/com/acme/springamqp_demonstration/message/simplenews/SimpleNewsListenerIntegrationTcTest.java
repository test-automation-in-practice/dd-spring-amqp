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

/**
 * FIXME - Modify this test after the <a href="https://github.com/spring-projects/spring-amqp/milestone/210">release
 * of Spring AMQP 3.0.5 on June 19, 2023</a> because of the:
 * <ul>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2456">Bug 2456</a></li>
 * <li><a href="https://github.com/spring-projects/spring-amqp/issues/2457">Fix 2457</a></li>
 * </ul>
 * Modify the part of {@link SimpleNewsWorkerTest} by replacing it with the verify command on the original
 * implementation {@link SimpleNewsWorker}.
 */
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

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${simple.news.exchange.name}")
  private String SIMPLE_NEWS_EXCHANGE_NAME;

  private final SimpleNewsListener simpleNewsListener;

  SimpleNewsListenerIntegrationTcTest(
      @Autowired SimpleNewsListener simpleNewsListener
  ) {
    this.simpleNewsListener = simpleNewsListener;
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
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Workaround for the "SimpleNewsWorkersListener".
    // Almost same implementation but with map for gathering the received messages.
    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () ->
                SimpleNewsWorkerTest.RECEIVED_MESSAGES.get(1).size() == 1
                    && SimpleNewsWorkerTest.RECEIVED_MESSAGES.get(2).size() == 1,
            is(true)
        );
  }

}

