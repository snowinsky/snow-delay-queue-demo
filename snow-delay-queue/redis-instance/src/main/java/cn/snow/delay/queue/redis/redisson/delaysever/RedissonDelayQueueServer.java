package cn.snow.delay.queue.redis.redisson.delaysever;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import cn.snow.delay.queue.redis.redisson.delayqueue.RedissonDelayQueue;
import cn.snow.delay.queue.redis.redisson.delayqueue.RedissonDelayQueueInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RedissonDelayQueueServer implements RedissonDelayQueueServerInterface {

    private final RedissonDelayQueueServerConfig delayQueueServerConfig;
    private final ConcurrentHashMap<String, RedissonDelayQueue<?>> ss = new ConcurrentHashMap<>();


    private RedissonClient redissonClient;

    @Override
    public void start(RedissonDelayQueueInterface.DelayMessageHandler<?> queueMessageHandler) {
        redissonClient = Redisson.create(delayQueueServerConfig.getRedissonConfig());
        Arrays.stream(delayQueueServerConfig.getDelayQueueNameArray()).forEach(a->{
            ss.computeIfAbsent(a, s -> {
                RedissonDelayQueue newQueue = new RedissonDelayQueue(redissonClient, s);
                newQueue.startConsumer(queueMessageHandler);
                return newQueue;
            });
        });
    }

    @Override
    public void stop() {
        ss.keySet().forEach(this::deleteDelayQueue);
        ss.clear();
    }

    @Override
    public void newDelayQueue(String queueName, RedissonDelayQueueInterface.DelayMessageHandler<?> queueMessageHandler) {
        ss.computeIfAbsent(queueName, s -> {
            RedissonDelayQueue newQueue = new RedissonDelayQueue(redissonClient, s);
            newQueue.startConsumer(queueMessageHandler);
            return newQueue;
        });
    }

    @Override
    public void deleteDelayQueue(String queueName) {
        RedissonDelayQueue<?> queue = ss.get(queueName);
        queue.destroyQueue();
        ss.remove(queueName);
    }

    @Override
    public RedissonDelayQueueInterface<?> getDelayQueue(String queueName) {
        return ss.get(queueName);
    }
}
