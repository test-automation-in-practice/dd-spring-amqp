# Production  ready questions

## Does Spring AMQP fits the requirements?

This is actually the first question which needs to be answered. Another approach is the usage of streams. Or as last option is not using this approach at all.

## TLC usage

Keys need to be created and also the strusted store as fist step which then need to be bound to the RabbitMQ system and the Spring AMQP application.

### RabbitMQ

[Here](https://www.rabbitmq.com/ssl.html) are instructions how to set the propertes in a configuration file.

### Spring AMQP

[Here](https://docs.spring.io/spring-amqp/reference/html/#rabbitconnectionfactorybean-configuring-ssl) are instructions how to set the propertes in a configuration file.

Here is an example how programmaticaly the configuration file can be read into the application:
```
//    Possible solution for TLS / SSL
RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
rabbitConnectionFactoryBean.setUseSSL(true);
//rabbitConnectionFactoryBean.setSslAlgorithm("TLSv1.2"); // should be default
//rabbitConnectionFactoryBean.setAutomaticRecoveryEnabled(true);
//rabbitConnectionFactoryBean.setKeyStore(keyStore); // use setSslPropertiesLocation
//rabbitConnectionFactoryBean.setTrustStore(trustStore); // use setSslPropertiesLocation
//rabbitConnectionFactoryBean.setKeyStorePassphrase(keyStorePassPhrase); // use setSslPropertiesLocation
//rabbitConnectionFactoryBean.setTrustStorePassphrase(trustStorePassPhrase); // use setSslPropertiesLocation
//    file where the ssl properties are like described in
//    https://docs.spring.io/spring-amqp/reference/html/#rabbitconnectionfactorybean-configuring-ssl
//    keyStore, trustStore, keyStore.passPhrase, trustStore.passPhrase
rabbitConnectionFactoryBean.setSslPropertiesLocation(new ClassPathResource("ssl.properties"));
//    setting some client properties is needed. optional and not relevant at the moment.
//rabbitConnectionFactoryBean.setClientProperties(Collections.<String, Object>singletonMap("foo", "bar"));
//rabbitConnectionFactoryBean.afterPropertiesSet();
CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitConnectionFactoryBean.getRabbitConnectionFactory());
```

## Queue-type decision

* Classic
* Quorum
  * https://www.rabbitmq.com/quorum-queues.html
    * Cluster
      * https://www.youtube.com/watch?v=FzqjtU2x6YA
      * https://www.youtube.com/watch?v=_lpDfMkxccc
* Streams?

## What should happen with not delivered messages?

Doe they need to be memorized, and how, and then resend later or just forgotten/consumed.

## Publisher Confirms

Do all messages need to be confirmed that they were successfully delivered?

## Credentials adjustments

The user **guest** can only access from local host and it is used for demonstration purposes
[Here](https://www.rabbitmq.com/access-control.html
) is described how new RabbitMQ user can be created and used.
In Spring AMQP the user and password are set on the connection factory with setter-methods.

## Should the Exchanges, Queues, and their Bindings need to be built automatically by Spring AMQP or manually on the RabittMQ?

Can the creation of the exchanges, queues and their binding be implemented in Spring AMQP as annotation like or should they be already created directly on the RabbitMQ broker.