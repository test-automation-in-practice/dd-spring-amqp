package com.acme.springamqp_demonstration.message.importanttopics;

import com.acme.springamqp_demonstration.message.DefaultContainerFactoryConfig;
import com.acme.springamqp_demonstration.message.MessageConverterBeans;
import com.acme.springamqp_demonstration.message.RabbitMqTestContainer;
import com.acme.springamqp_demonstration.message.RabbitTemplateTestBeans;
import com.acme.springamqp_demonstration.message.importanttopics.model.ImportantTopic;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
@DirtiesContext
@PropertySource("classpath:important-topics.properties")
@ContextConfiguration(
		classes = {
				MessageConverterBeans.class,
				RabbitTemplateTestBeans.class,
				DefaultContainerFactoryConfig.class,
				ImportantTopicsConfig.class,
				ImportantTopicsGeneralListener.class,
				ImportTopicsListenerSpyTcTest.Config.class
		}
)
public class ImportTopicsListenerSpyTcTest extends RabbitMqTestContainer {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private RabbitListenerTestHarness harness;

	@Value("${important.topics.queue.name.general1}")
	private String IMPORTANT_TOPIC_TYPES_QUEUE_NAME;

	@Test
	public void testingMultiHandler() throws Exception {
		ImportantTopicsGeneralListener importantTopicsGeneralListener =
				this.harness.getSpy("importantTopicsGeneralMultiMethodListener");
		assertThat(importantTopicsGeneralListener).isNotNull();

		LatchCountDownAndCallRealMethodAnswer answer =
				this.harness.getLatchAnswerFor("importantTopicsGeneralMultiMethodListener", 4);
		willAnswer(answer).given(importantTopicsGeneralListener).receiveGeneralTopicsString(anyString());
		willAnswer(answer).given(importantTopicsGeneralListener).receiveGeneralTopics(any());

		String bar = "bar";
		ImportantTopic sm1 = new ImportantTopic(bar, bar);
		String baz = "baz";
		ImportantTopic sm2 = new ImportantTopic(baz, baz);
		this.rabbitTemplate.convertAndSend(IMPORTANT_TOPIC_TYPES_QUEUE_NAME, bar);
		this.rabbitTemplate.convertAndSend(IMPORTANT_TOPIC_TYPES_QUEUE_NAME, baz);
		this.rabbitTemplate.convertAndSend(IMPORTANT_TOPIC_TYPES_QUEUE_NAME, sm1);
		this.rabbitTemplate.convertAndSend(IMPORTANT_TOPIC_TYPES_QUEUE_NAME, sm2);

		assertThat(answer.await(10)).isTrue();
		verify(importantTopicsGeneralListener).receiveGeneralTopicsString(bar);
		verify(importantTopicsGeneralListener).receiveGeneralTopicsString(baz);
		verify(importantTopicsGeneralListener).receiveGeneralTopics(sm1);
		verify(importantTopicsGeneralListener).receiveGeneralTopics(sm2);
	}

	@Configuration
	@RabbitListenerTest
	public static class Config {

		@Bean
		public ImportantTopicsGeneralListener importantTopicsGeneralListener() {
			return new ImportantTopicsGeneralListener();
		}

	}

}
