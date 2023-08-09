package cn.snow.delay.queue.redis.redisson.delayqueue;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedissonDelayQueue<T> implements RedissonDelayQueueInterface<T> {

    private final String queueName;
    private final RedissonClient redissonClient;
    RBlockingQueue<T> blockingQueue;
    RDelayedQueue<T> delayQueue;

    public RedissonDelayQueue(RedissonClient redissonClient, String queueName) {
        this.queueName = queueName;
        this.redissonClient = redissonClient;
    }

    /**
     * 销毁redis中的delayQueue
     */
    @Override
    public void destroyQueue() {
        if (delayQueue != null && delayQueue.isExists()) {
            delayQueue.destroy();
        }
    }

    /**
     * 获取可以消费的delayQueue
     * @return
     */
    public RBlockingQueue<T> getBlockingQueue() {
        if (blockingQueue == null) {
            blockingQueue = redissonClient.getBlockingDeque(queueName);
            delayQueue = redissonClient.getDelayedQueue(blockingQueue);
        }
        return blockingQueue;
    }

    /**
     * 返回可以发送的delayQueue
     * @return
     */
    @Override
    public RDelayedQueue<T> getDelayQueue() {
        if (delayQueue == null) {
            blockingQueue = redissonClient.getBlockingDeque(queueName);
            delayQueue = redissonClient.getDelayedQueue(blockingQueue);
        }
        return delayQueue;
    }

    /**
     * 发送延迟消息到delayQueue
     * @param delayMessage
     * @param delayDuration
     */
    @Override
    public void sentDelayMessageToQueue(T delayMessage, Duration delayDuration) {
        if (delayMessage == null) {
            return;
        }
        getDelayQueue().offer(delayMessage, delayDuration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 当其他途径处理了延迟中的消息时，可以调用该接口从延迟队列中删除该消息，避免该消息被重复处理
     * @param delayMessage
     * @return
     */
    @Override
    public boolean removeDelayMessageFromQueue(T delayMessage) {
        if (delayMessage == null) {
            throw new NullPointerException("the removed delayMessage is null");
        }
        return getDelayQueue().removeIf(t -> t != null && t.equals(delayMessage));
    }

    /**
     * 启动延迟队列的消费者服务
     * @param delayMessageHandler
     */
    @Override
    public void startConsumer(DelayMessageHandler<T> delayMessageHandler) {
        Thread delayQueueConsumerThread = new Thread(() -> {
            RBlockingQueue<T> bq = getBlockingQueue();
            while (true) {
                try {
                    T delayMessage = bq.take();
                    handleDelayMessage(delayMessageHandler, delayMessage);
                } catch (InterruptedException e) {
                    log.error("take delayMessage from delayQueue fail", e);
                    Thread.currentThread().interrupt();
                }
            }
        });
        delayQueueConsumerThread.setName("DelayQueue_" + queueName + "_Consumer_Thread_" + Thread.currentThread().getId());
        delayQueueConsumerThread.setDaemon(false);
        delayQueueConsumerThread.setUncaughtExceptionHandler((t, e) -> log.error("DelayMessage delay handle failure. thread id={}, thread name={}", t.getId(), t.getName(), e));
        delayQueueConsumerThread.start();
        log.info("####  ########################################## ####");
        log.info("####  delayQueueConsumer start... queueName={} ####", queueName);
        log.info("####  ########################################## ####");
    }

    private void handleDelayMessage(DelayMessageHandler<T> delayMessageHandler, T delayMessage) {
        try {
            delayMessageHandler.handleSuccess(delayMessage);
        } catch (Exception e) {
            log.error("handle delayMessage failure", e);
            delayMessageHandler.handleFailure(delayMessage, e, getDelayQueue());
        }
    }


}
