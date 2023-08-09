package cn.snow.delay.queue.redis.redisson.delaysever;

public interface RedissonDelayQueueServer {

    void start();
    void stop();
    void newDelayQueue(String queueName);
    void deleteDelayQueue(String queueName);
}
