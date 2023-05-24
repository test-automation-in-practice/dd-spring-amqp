package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:important-topics.properties")
@Service
public class ImportantTopicsGeneralForErrorListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsGeneralForErrorListener.class);

  @RabbitListener(
      queues = { "${important.topics.queue.name.general2}" },
      containerFactory = "customImpTopRepublishContainerFactory"
  )
  public void receiveGeneralTopicsError(final ImportantTopic importantTopic) {
    LOGGER.info("Receiving Important Topics with topic general: {} with date time {}",
        importantTopic.messageContent(), importantTopic.currentDateTime());
    throw new RuntimeException();
  }

  @RabbitListener(
      queues = { "${important.topics.queue.name.general2.dlq}" },
      containerFactory = "defaultRetryContainerFactory"
  )
  public void receiveGeneralTopicsDlq(final ImportantTopic importantTopic) {
    LOGGER.info("Receiving Important Topics with topic general {} failed!", importantTopic.currentDateTime());
  }

}
