package cn.snow.delay.queue.redis.redisson.delaysever;

import cn.snow.delay.queue.redis.redisson.delayqueue.RedissonDelayQueueInterface;

public interface RedissonDelayQueueServerInterface {

    void start(RedissonDelayQueueInterface.DelayMessageHandler<?> queueMessageHandler);
    void stop();
    void newDelayQueue(String queueName, RedissonDelayQueueInterface.DelayMessageHandler<?> queueMessageHandler);
    void deleteDelayQueue(String queueName);

    RedissonDelayQueueInterface getDelayQueue(String queueName);
}
