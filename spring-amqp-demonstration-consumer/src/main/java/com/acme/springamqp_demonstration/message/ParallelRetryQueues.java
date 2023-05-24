package com.acme.springamqp_demonstration.message;

import org.springframework.amqp.core.Queue;

public class ParallelRetryQueues {
    private final Queue[] queues;
    private final long initialInterval;
    private final double factor;
    private final long maxWait;

    public ParallelRetryQueues(long initialInterval, double factor, long maxWait, Queue... queues) {
        this.queues = queues;
        this.initialInterval = initialInterval;
        this.factor = factor;
        this.maxWait = maxWait;
    }

    public boolean retriesExhausted(int retry) {
        return retry >= queues.length;
    }

    public String getQueueName(int retry) {
        return queues[retry].getName();
    }

    public long getTimeToWait(int retry) {
        double time = initialInterval * Math.pow(factor, retry);
        if (time > maxWait) {
            return maxWait;
        }

        return (long) time;
    }
}