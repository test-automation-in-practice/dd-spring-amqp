package com.acme.springamqp_demonstration.message;

import com.rabbitmq.client.Channel;
import jakarta.annotation.Nonnull;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParallelRetryQueuesInterceptor implements MethodInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParallelRetryQueuesInterceptor.class);

  public static final String HEADER_X_RETRIED_COUNT = "x-retried-count";
  public static final String HEADER_X_ORIGINAL_EXCHANGE = "x-original-exchange";
  public static final String HEADER_X_ORIGINAL_ROUTING_KEY = "x-original-routing-key";

  private final RabbitTemplate rabbitTemplate;

  private final ParallelRetryQueues retryQueues;

  public ParallelRetryQueuesInterceptor(RabbitTemplate rabbitTemplate, ParallelRetryQueues retryQueues) {
    this.rabbitTemplate = rabbitTemplate;
    this.retryQueues = retryQueues;
  }

  @Override
  public Object invoke(@Nonnull MethodInvocation invocation) {
    return tryConsume(
        invocation,
        mac -> {
          try {
            mac.channel.basicAck(mac.message.getMessageProperties()
                .getDeliveryTag(), false);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        (mac, e) ->
        {
          try {
            sendToNextRetryQueue(mac, tryGetRetryCountOrFail(mac, e));
          } catch (Throwable t) {
            throw new RuntimeException(t);
          }
        });
  }

  private Object tryConsume(
      MethodInvocation invocation,
      Consumer<MessageAndChannel> successHandler,
      BiConsumer<MessageAndChannel, Throwable> errorHandler) {
    MessageAndChannel mac =
        new MessageAndChannel((Message) invocation.getArguments()[1], (Channel) invocation.getArguments()[0]);
    Object ret = null;
    try {
      ret = invocation.proceed();
      successHandler.accept(mac);
    } catch (Throwable e) {
      errorHandler.accept(mac, e);
    }
    return ret;
  }

  private int tryGetRetryCountOrFail(MessageAndChannel mac, Throwable originalError) throws Throwable {
    MessageProperties props = mac.message.getMessageProperties();
    String xRetriedCountHeader = props.getHeader(HEADER_X_RETRIED_COUNT);
    final int xRetriedCount = xRetriedCountHeader == null ? 0 : Integer.parseInt(xRetriedCountHeader);
    if (retryQueues.retriesExhausted(xRetriedCount)) {
      mac.channel.basicReject(props.getDeliveryTag(), false);
      throw originalError;
    }
    return xRetriedCount;
  }

  private void sendToNextRetryQueue(MessageAndChannel mac, int retryCount) throws Exception {
    String retryQueueName = retryQueues.getQueueName(retryCount);
    LOGGER.info("RetryQueuesInterceptor::retryQueueName '{}' message '{}'",
        retryQueueName, new String(mac.message.getBody(), StandardCharsets.UTF_8));
    rabbitTemplate.convertAndSend(retryQueueName, mac.message, m -> {
      MessageProperties props = m.getMessageProperties();
      props.setExpiration(String.valueOf(retryQueues.getTimeToWait(retryCount)));
      props.setHeader(HEADER_X_RETRIED_COUNT, String.valueOf(retryCount + 1));
      props.setHeader(HEADER_X_ORIGINAL_EXCHANGE, props.getReceivedExchange());
      props.setHeader(HEADER_X_ORIGINAL_ROUTING_KEY, props.getReceivedRoutingKey());
      return m;
    });
    mac.channel.basicReject(mac.message.getMessageProperties().getDeliveryTag(), false);
  }

  private record MessageAndChannel(Message message, Channel channel) {
  }
}