package cn.snow.delay.queue.redis.redisson.delayqueue;

import java.time.Duration;

import org.redisson.api.RDelayedQueue;

public interface RedissonDelayQueueInterface<T> {

    /**
     * 有则返回，无则创建
     * @return
     */
    RDelayedQueue<T> getDelayQueue();

    /**
     * 删除基础设置里的延迟队列
     */
    void destroyQueue();

    /**
     * 发送延迟消息到延迟队列，指定延迟时间
     * @param delayMessage
     * @param delayDuration
     */
    void sentDelayMessageToQueue(T delayMessage, Duration delayDuration);

    /**
     * 删除延迟队列里的某一个消息
     * @param delayMessage
     * @return
     */
    boolean removeDelayMessageFromQueue(T delayMessage);

    /**
     * 开启延迟队列消费者
     * @param delayMessageHandler
     */
    void startConsumer(DelayMessageHandler<T> delayMessageHandler);

    interface DelayMessageHandler<T> {
        void handleSuccess(T delayMessage) throws Exception;

        void handleFailure(T delayMessage, Throwable e, RDelayedQueue<T> delayedQueue);
    }
}
