package cn.snow.delay.queue.single.jdk.delay_queue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdkDelayQueue {

    /**
     * 入队：
     * add 返回boolean，不阻塞
     * put 返回void，不阻塞
     * offer 上面两个的实际调用
     * 出队：
     * poll 不阻塞，没有就返回null
     * take 阻塞，有结果才返回
     * poll+时间 阻塞，但有超时时间
     * <p>
     * 查询：
     * peek：返回第一个元素
     * size：返回总量
     * <p>
     * 清空：
     * clear
     */
    private static final DelayQueue<DelayMessage> DELAY_QUEUE = new DelayQueue();

    private static final Producer P = new Producer();
    private static final Consumer C = new Consumer();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            P.sendDelayMessage(new DelayMessage(1, 2000L));
            P.sendDelayMessage(new DelayMessage(2, 1000L));
            P.sendDelayMessage(new DelayMessage(3, 3000L));
        }).start();

        C.accept();

    }

    public static class Producer {
        public void sendDelayMessage(DelayMessage delayMessage) {
            DELAY_QUEUE.add(delayMessage);
        }
    }

    public static class Consumer {
        @SuppressWarnings("all")
        public void accept() throws InterruptedException {
            while (true) {
                DelayMessage d = DELAY_QUEUE.take();
                System.out.println(d);
            }
        }
    }

    @Data
    public static class DelayMessage implements Delayed {

        private final int messageId;
        private final LocalDateTime addQueueTime;
        private final long delayMillis;
        private Object messageContent;

        public DelayMessage(int messageId, long delayMillis) {
            this.delayMillis = delayMillis;
            this.messageId = messageId;
            addQueueTime = LocalDateTime.now();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(Duration.between(LocalDateTime.now(), addQueueTime.plus(Duration.ofMillis(delayMillis))));
        }

        /**
         * 比较器，决定了哪个消息先出队，这里的实现是将最先到期的先出队
         * 剩余时间都一样，先加入队列的先出队
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(Delayed o) {
            long ret = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            if (ret == 0) {
                return getAddQueueTime().compareTo(((DelayMessage) o).getAddQueueTime());
            } else if (ret > 0) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", DelayMessage.class.getSimpleName() + "[", "]")
                    .add("messageId=" + messageId)
                    .add("addQueueTime=" + addQueueTime)
                    .add("delayMillis=" + delayMillis)
                    .add("currentTime=" + LocalDateTime.now())
                    .toString();
        }
    }
}
