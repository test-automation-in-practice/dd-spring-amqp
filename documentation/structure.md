# Structure of the project

## Introduction

The "Spring AMQP Demostrantion" project is separated into three parts which can be seen as separate use cases. The main property which separated the cases from each other is the exchange type.

More about the different property types can be found [here](/documentation/use-case-dimensions.md).

## Implemented Cases

Folowing use cases were implemented:

### Simple News

The idea was to test simple message communication. Follwing characteristics are part of this case:

* Message Queuing Patterns
  * Simple producer/consumer
  * Work Queues
  * Publish/Subsribe
  * RPC
* Exchange Type
  * Fanout
* Queue Type
  * Classic
* Message Type
  * Text
  * Byte Array
  * JSON
* Error Handling Strategies
  * None

### Important Topics

Topic exchange type was used here with dead letter error strategy. Also the extra parallel message requeuing strategy from the [baeldung](https://www.baeldung.com/spring-amqp-exponential-backoff) page was used here.

* Message Queuing Patterns
  * Topics
* Exchange Type
  * Topics
* Queue Type
  * Classic
* Message Type
  * String
  * Serialized Object
* Error Handling Strategies
  * Dead letter exchange and queue
* Extra
  * Parallel message requeuing
  * Reading string and objects from a queue with two handlers

### Special Messages

Header exchange type was used here with parking lot error strategy.

* Message Queuing Patterns
  * Header (not mentioned explicitly at [RabbitMQ tutorials](https://www.rabbitmq.com/getstarted.html))
* Exchange Type
  * Header
* Queue Type
  * Classic
* Message Type
  * JSON
* Error Handling Strategies
  * Parking lot


## Testing

Several tests were also implemented, based on the simple news part.
One unit test for the sender/publisher and few integration test with and without the **Test Container** library for sender and listener.