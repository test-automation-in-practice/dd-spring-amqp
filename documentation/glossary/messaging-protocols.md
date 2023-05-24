# Messaging Protocols

Source: https://smartechyblog.wordpress.com/2016/11/04/amqp-vs-mqtt-vs-stomp/

Three famous messaging protocols exist:
* MQTT
* STOMP
* AMPQ

## MQTT

It provides publish-and-subscribe messaging (no queues, in spite of the name) and was specifically designed for resource-constrained devices and low bandwidth, high latency networks such as dial up lines and satellite links, for example.

One of the advantages MQTT has over more full-featured “enterprise messaging” brokers is that its intentionally low footprint makes it ideal for today’s mobile and developing “Internet of Things” style applications.

## STOMP

STOMP (Simple/Streaming Text Oriented Messaging Protocol) is the only one of these three protocols to be text-based, making it more analogous to HTTP in terms of how it looks under the covers.

One of the most interesting examples is with RabbitMQ Web Stomp which allows you to expose messaging in a browser through websockets.

## AMPQ

AMQP, which stands for Advanced Message Queuing Protocol, was designed as an open replacement for existing proprietary messaging middleware. Two of the most important reasons to use AMQP are reliability and interoperability. 

As the name implies, it provides a wide range of features related to messaging, including reliable queuing, topic-based publish-and-subscribe messaging, flexible routing, transactions, and security. AMQP exchanges route messages directly—in fanout form, by topic, and also based on headers.

***

## [RabbitMQ](rabbitmq.md)

All three (STOMP, MQTT, or AMQP) are supported by the RabbitMQ broker, making it an ideal choice for interoperability between applications. The plugin architecture also enables RabbitMQ to evolve to support additional or updated versions of these protocols in the future.