package cn.snow.delay.queue.redis.redisson;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import cn.snow.delay.queue.single.jdk.inter.SimpleDelayMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedissonDelayQueue {

    public static final RedissonClient REDISSON_CLIENT = init();

    private static RedissonClient init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(1)
                .setTimeout(3000)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(30);
        return Redisson.create(config);
    }

    public static class Producer {
        public void send(SimpleDelayMessage delayMessage) {
            final RBlockingDeque<SimpleDelayMessage> blockingQueue = REDISSON_CLIENT.getBlockingDeque("simple_delay_queue");
            final RDelayedQueue<SimpleDelayMessage> delayQueue = REDISSON_CLIENT.getDelayedQueue(blockingQueue);
            delayQueue.offer(delayMessage, delayMessage.getDelayDuration().toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    public static class Consumer {
        public void consume() throws InterruptedException {
            final RBlockingDeque<SimpleDelayMessage> blockingQueue = REDISSON_CLIENT.getBlockingDeque("simple_delay_queue");
            final RDelayedQueue<SimpleDelayMessage> delayQueue = REDISSON_CLIENT.getDelayedQueue(blockingQueue);
            for (; ; ) {
                SimpleDelayMessage sm = blockingQueue.take();
                sm.setActualConsumeTime(LocalDateTime.now());
                log.info("##### delay consume time={}", Duration.between(sm.getExpireTime(), sm.getActualConsumeTime()).toMillis());
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {

        Producer p = new Producer();
        for (int i = 0; i < 5000; i++) {
            new Thread(() -> {
                p.send(new SimpleDelayMessage(System.nanoTime() + "", 3000L));
                p.send(new SimpleDelayMessage(System.nanoTime() + "", 1000L));
                p.send(new SimpleDelayMessage(System.nanoTime() + "", 2000L));
            }).start();
        }


        Consumer c = new Consumer();
        c.consume();
    }
}
