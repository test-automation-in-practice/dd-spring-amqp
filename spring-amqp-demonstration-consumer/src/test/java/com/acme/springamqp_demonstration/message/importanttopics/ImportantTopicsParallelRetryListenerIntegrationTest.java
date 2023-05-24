package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * A RabbitMQ application should be running in background.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(
    classes = {
        MessageConverterBeans.class, // loads the message converters.
        DefaultContainerFactoryConfig.class,
        ImportantTopicsParallelRetryConfig.class, // creates the exchange and the queue if not already created.
        ImportantTopicsParallelRetryListenerConfiguration.class, // loads the logic for the retry queues container factory.
        RabbitTemplateTestBeans.class, // creates rabbitTemplate instance for auto writing.
        ImportantTopicsParallelRetryListener.class // loads the listeners.
    }
)
@Ignore
@Disabled
public class ImportantTopicsParallelRetryListenerIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsParallelRetryListenerIntegrationTest.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${important.topics.exchange.name.pr}")
    private String IMPORTANT_TOPICS_EXCHANGE_NAME_PR;

    @Disabled
    @Test
    public void whenSendToNonBlockingQueue_thenAllMessageProcessed() throws Exception {
        int nb = 2;
        for (int i = 1; i <= nb; i++) {
            rabbitTemplate.convertAndSend(
                IMPORTANT_TOPICS_EXCHANGE_NAME_PR,
                "com.acme.general",
                new ImportantTopic(IMPORTANT_TOPICS_EXCHANGE_NAME_PR, String.valueOf(i))
            );
        }
        Thread.sleep(30000);
        LOGGER.info("The manual integration test ends!!!");
    }

}
