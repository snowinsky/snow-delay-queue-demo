package cn.snow.delay.queue.redis.redisson.delaysever;

import org.redisson.config.Config;

import lombok.Data;

@Data
public class RedissonDelayQueueServerConfig {

    private Config redissonConfig;

    private String[] delayQueueNameArray;
}
