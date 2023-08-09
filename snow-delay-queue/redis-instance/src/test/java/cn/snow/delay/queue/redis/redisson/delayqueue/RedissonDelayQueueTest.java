package cn.snow.delay.queue.redis.redisson.delayqueue;

import java.time.Duration;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedissonDelayQueueTest {
    public static final RedissonClient REDISSON_CLIENT = init();

    private static RedissonClient init() {
        Config config = new Config();
        config.setCodec(new StringCodec());
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(1)
                .setTimeout(3000)
                .setConnectionPoolSize(64)
                .setConnectionMinimumIdleSize(30);
        return Redisson.create(config);
    }

    @Test
    public void testHandleNoWaitConsume() throws InterruptedException {
        RedissonDelayQueue<String> red = new RedissonDelayQueue<>(REDISSON_CLIENT, "test_delay_queue");
        red.startConsumer(new RedissonDelayQueue.DelayMessageHandler<String>() {
            @Override
            public void handleSuccess(String delayMessage) throws Exception {
                log.info("consume the message={}", delayMessage);
            }

            @Override
            public void handleFailure(String delayMessage, Throwable e, RDelayedQueue<String> delayedQueue) {
                log.error("consume the message={} fail by {}", delayMessage, e.getMessage());
            }
        });
        red.sentDelayMessageToQueue("id=1, delay=3", Duration.ofSeconds(3));
        red.sentDelayMessageToQueue("id=2, delay=1", Duration.ofSeconds(1));
        red.sentDelayMessageToQueue("id=3, delay=2", Duration.ofSeconds(2));

    }


    @Test
    public void testHandleSuccess() throws InterruptedException {
        RedissonDelayQueue<String> red = new RedissonDelayQueue<>(REDISSON_CLIENT, "test_delay_queue");
        red.startConsumer(new RedissonDelayQueue.DelayMessageHandler<String>() {
            @Override
            public void handleSuccess(String delayMessage) throws Exception {
                log.info("consume the message={}", delayMessage);
            }

            @Override
            public void handleFailure(String delayMessage, Throwable e, RDelayedQueue<String> delayedQueue) {
                log.error("consume the message={} fail by {}", delayMessage, e.getMessage());
            }
        });
        red.sentDelayMessageToQueue("id=1, delay=3", Duration.ofSeconds(3));
        red.sentDelayMessageToQueue("id=2, delay=1", Duration.ofSeconds(1));
        red.sentDelayMessageToQueue("id=3, delay=2", Duration.ofSeconds(2));

        Thread.sleep(60000);
    }

    @Test
    public void testHandleFailure() throws InterruptedException {
        RedissonDelayQueue<String> red = new RedissonDelayQueue<>(REDISSON_CLIENT, "test_delay_queue");
        red.startConsumer(new RedissonDelayQueue.DelayMessageHandler<String>() {
            @Override
            public void handleSuccess(String delayMessage) throws Exception {
                log.info("consume the message={}", delayMessage);
                throw new IllegalArgumentException("sss");
            }

            @Override
            public void handleFailure(String delayMessage, Throwable e, RDelayedQueue<String> delayedQueue) {
                log.error("consume the message={} fail by {}", delayMessage, e.getMessage());
            }
        });
        red.sentDelayMessageToQueue("id=1, delay=3", Duration.ofSeconds(3));
        red.sentDelayMessageToQueue("id=2, delay=1", Duration.ofSeconds(1));
        red.sentDelayMessageToQueue("id=3, delay=2", Duration.ofSeconds(2));

        Thread.sleep(60000);
    }

    @Test
    public void testHandleRemoveMsg() throws InterruptedException{
        RedissonDelayQueue<String> red = new RedissonDelayQueue<>(REDISSON_CLIENT, "test_delay_queue");
        red.startConsumer(new RedissonDelayQueue.DelayMessageHandler<String>() {
            @Override
            public void handleSuccess(String delayMessage) throws Exception {
                log.info("consume the message={}", delayMessage);
                throw new IllegalArgumentException("sss");
            }

            @Override
            public void handleFailure(String delayMessage, Throwable e, RDelayedQueue<String> delayedQueue) {
                log.error("consume the message={} fail by {}", delayMessage, e.getMessage());
            }
        });
        red.sentDelayMessageToQueue("id=1, delay=3", Duration.ofSeconds(3));
        red.sentDelayMessageToQueue("id=2, delay=1", Duration.ofSeconds(1));
        red.sentDelayMessageToQueue("id=3, delay=2", Duration.ofSeconds(2));

        log.info("during delayMessageHandling, remove the message. return={}", red.removeDelayMessageFromQueue("id=1, delay=3"));
        log.info("during delayMessageHandling, remove the message. return={}", red.removeDelayMessageFromQueue("id=2, delay=1"));
        log.info("during delayMessageHandling, remove the message. return={}", red.removeDelayMessageFromQueue("id=3, delay=2"));

        Thread.sleep(60000);
    }

}