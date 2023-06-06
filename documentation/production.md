# Production-ready questions

## Does Spring AMQP fit the requirements?

This is the first question that needs to be answered.

Source:
* https://www.rabbitmq.com/quorum-queues.html
* https://codeopinion.com/avoiding-a-queue-backlog-disaster-with-backpressure-flow-control/

Questions:
 * The rate at which you’re producing messages. 
 * The rate at which you can consume messages. 
 * How long are messages sitting in the queue?
   * https://blog.rabbitmq.com/posts/2012/05/some-queuing-theory-throughput-latency-and-bandwidth 
 * What’s the lead time, from when it was produced to when it was consumed to be processed? 
 * What’s the processing time, and how long does it take to consume a specific message?

### Decisions features

* Streams
  * https://www.rabbitmq.com/streams.html
  * If the queue backlog is very long then streams are the best solution.
* Quorum Queues
  * Based on the Raft consensus algorithm: https://raft.github.io/
  * https://www.rabbitmq.com/quorum-queues.html
  * Quorum queues are purpose-built by design. They are not designed to be used for every problem. Their intended use is for topologies where queues exist for a long time and are critical to certain aspects of system operation, therefore fault tolerance and data safety are more important than, say, the lowest possible latency and advanced queue features.
  * Publisher confirms will only be issued once a published message has been successfully replicated to a quorum of nodes and is considered "safe" within the context of the system.
  * **Examples** would be incoming orders in a sales system or votes cast in an election system where potentially losing messages would have a significant impact on system correctness and function.
* Classic Queues
  * https://www.rabbitmq.com/classic-queues.html
  * Temporary nature of queues: transient or exclusive queues, high queue churn (declaration and deletion rates)
  * Lowest possible latency: the underlying consensus algorithm has an inherently higher latency due to its data safety features
  * When data safety is not a priority (e.g. applications do not use manual acknowledgments and publisher confirms are not used)
  * **Examples** would be stock tickers and instant messaging systems.

Quorum Queues configuration

Using quorum queues implies a RabbitMQ cluster with at least three nodes. How to configure and install a cluster for quorum queues can be seen in the videos:
* https://www.youtube.com/watch?v=FzqjtU2x6YA
* https://www.youtube.com/watch?v=_lpDfMkxccc

**Note**: 
  * Use TTL in quorum queues. It was a very desired missing feature for the quorum queues! 
    * https://blog.rabbitmq.com/posts/2022/05/rabbitmq-3.10-release-overview/
    * It is set to the message properties: `message.getMessageProperties().setExpiration(ttl.toString());`
  * Lazy mode is the default mode:
    * As of RabbitMQ in version 3.12 there is no longer a distinction between lazy and non-lazy (default) queue mode for classic queues.
    * It is recommended to be switched to v2 queues and it is expected that they will be the default queues version of RabbitMQ 3.13.

### Publisher Confirms and Manual Acknowledgement

For message delivering safety checking the delivered content to and from the RabbitMQ queue two features exist.

#### Publisher Confirms

For unroutable messages, the broker will issue a confirmation once the exchange verifies a message won't route to any queue (returns an empty list of queues). If the message is also published as mandatory, the basic.return is sent to the client before basic.ack. The same is true for negative acknowledgments (basic.nack).

For routable messages, the basic.ack is sent when a message has been accepted by all the queues. For persistent messages routed to durable queues, this means persisting to disk. For quorum queues, this means that a quorum replicas have accepted and confirmed the message to the elected leader.

##### How to
The feature which checks the data safety from sending the message to the queue is known as Publisher Confirms. 
1. In Spring AMQP the `CachingConnectionFactory` is used for that purpose and its published type is set to `CORRELATED`.
2. `RabbitTemplate` instance is created with the factory and the mandatory property is set to true, also a callback function is set to the confirm callback property. This way you can get callback messages on the rabbit template when a message was acknowledged.
3. Another way to get a callback is to send an argument of `CorrelationData`-object to the send method and invoke its `getFuture().get(10, TimeUnit.SECONDS).isAck()` method.

#### Manual Acknowledgement

##### How to
Manual Acknowledgement is set on the `@RabbitListener` as property `ackMode = "MANUAL"` and two more arguments are needed on the method which are a `Channel` and the delivery-tag from the header `@Header(AmqpHeaders.DELIVERY_TAG) long`.

**Note**: The SimpleNewsPublisherConfirms example shows how these features can be implemented.
**Note**: Consider also the difference in the channel-prefetch behavior of the automatic and manual acknowledgment.

#### Table of queue usage
|Queue Type|Publisher Confirms|Manual Acknowledgement|Description|
|-|-|-|-|
|Quorum|yes|yes|Most data safety but least data throughput|
|Classic|no|no|Least data safety but least data throughput|

## What should happen with not delivered messages?

After all retries the messages are not delivered and the question is, what to do with them? Do they need to be memorized, and how, and then resend later or just forgotten/consumed? This question came after the usage of the classic queues.

## Should the Exchanges, Queues, and their Bindings need to be built automatically by Spring AMQP or manually on the RabittMQ?

Can the creation of the exchanges, queues, and their binding be implemented in Spring AMQP as annotation or should they be first created directly on the RabbitMQ broker?

***

It is very convenient in the development phase, that the project manages those RabbitMQ objects.
In production, I'd recommend managing them on the broker side, when some permissions and other restrictions may apply.
If they exist on a broker, Spring AMQP does not override them.
This way the code might remain in your project and it won't hurt in production.
Unless some mismatched basic properties are different.

In the end, both variants are OK: might fully depend on the development team's preferences.
For example, Spring Cloud Stream Binder abstraction even has a dedicated feature to populate (or modify) those broker objects for bindings.


## TLS usage

Keys need to be created and also the trusted store is the first step which then needs to be bound to the RabbitMQ system and the Spring AMQP application.

### TLS configuration in RabbitMQ

[Here](https://www.rabbitmq.com/ssl.html) are instructions on how to set the properties in a configuration file.

### TLS configuration in Spring AMQP

[Here](https://docs.spring.io/spring-amqp/reference/html/#rabbitconnectionfactorybean-configuring-ssl) are instructions on how to set the properties in a configuration file.

Here is an example of how programmatically the configuration file can be read into the application:
```
//    Possible solution for TLS / SSL
RabbitConnectionFactoryBean rabbitConnectionFactoryBean = new RabbitConnectionFactoryBean();
rabbitConnectionFactoryBean.setUseSSL(true);
//    file where the SSL properties are described in
//    https://docs.spring.io/spring-amqp/reference/html/#rabbitconnectionfactorybean-configuring-ssl
//    keyStore, trustStore, keyStore.passPhrase, trustStore.passPhrase
rabbitConnectionFactoryBean.setSslPropertiesLocation(new ClassPathResource("ssl.properties"));
CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rabbitConnectionFactoryBean.getRabbitConnectionFactory());
```

### TLS configuration in RabbitMQ Cluster

[Here](https://rabbitmq.com/clustering-ssl.html) is a page how on a cluster should be configured with a TLC communication.

## Credentials adjustments

The user **guest** can only access from the local host and it is used for demonstration purposes
[Here](https://www.rabbitmq.com/access-control.html
) is described how new RabbitMQ users can be created and used.
In Spring AMQP the user and password are set on the "connection factory" with setter methods.
