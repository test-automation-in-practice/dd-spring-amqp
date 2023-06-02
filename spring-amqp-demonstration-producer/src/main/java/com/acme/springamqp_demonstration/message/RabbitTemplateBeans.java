package com.acme.springamqp_demonstration.message;

import com.acme.springamqp_demonstration.message.importanttopics.ImportantTopicsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RabbitTemplateBeans {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportantTopicsSender.class);

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    return new RabbitTemplate(connectionFactory);
  }

  @Bean
  public RabbitTemplate jsonRabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jackson2Converter());
    return template;
  }

  @Bean
  public MessageConverter jackson2Converter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public CachingConnectionFactory confirmingCachingConnectionFactory() {
//    Possible solution for TLS / SSL
//    RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
//    rabbitConnectionFactoryBean.setUseSSL(true);
//    file where the ssl properties are like described in
//    https://docs.spring.io/spring-amqp/reference/html/#rabbitconnectionfactorybean-configuring-ssl
//    keyStore, trustStore, keyStore.passPhrase, trustStore.passPhrase
//    rabbitConnectionFactoryBean.setSslPropertiesLocation(new ClassPathResource("ssl.properties"));
//    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitConnectionFactoryBean.getRabbitConnectionFactory());
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(getHost(), getPort());
    cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
    return cachingConnectionFactory;
  }

  @Bean
  public RabbitTemplate confirmingRabbitTemplate(CachingConnectionFactory confirmingCachingConnectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(confirmingCachingConnectionFactory);
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setConfirmCallback(
        (correlationData, ack, cause) ->
            LOGGER.info("Message feedback - correlationData: {} ack: {}", correlationData, ack)
    );
    return rabbitTemplate;
  }

  String getHost() {
    String host = System.getProperty("spring.rabbitmq.host");
    return Objects.nonNull(host) && !host.isEmpty() ? host : "localhost";
  }

  int getPort() {
    try {
      return Integer.parseInt(String.valueOf(System.getProperty("spring.rabbitmq.port")));
    } catch (NumberFormatException e) {
      return 5672;
    }
  }

}
