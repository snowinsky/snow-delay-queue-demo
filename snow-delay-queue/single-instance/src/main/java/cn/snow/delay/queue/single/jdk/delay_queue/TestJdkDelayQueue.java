package cn.snow.delay.queue.single.jdk.delay_queue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import cn.snow.delay.queue.single.jdk.inter.IDelayMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestJdkDelayQueue {

    public static class WrapperDelayMessage implements IDelayMessage, Delayed {

        private final JdkDelayQueue.DelayMessage delayMessage;

        private LocalDateTime actualConsumeTime;

        public WrapperDelayMessage(JdkDelayQueue.DelayMessage delayMessage) {
            this.delayMessage = delayMessage;
        }

        @Override
        public void setDelayMessageCode(String messageCode) {
            throw new UnsupportedOperationException("cannot change");
        }

        @Override
        public void setDelayMessageContent(Object messageContent) {
            delayMessage.setMessageContent(messageContent);
        }

        @Override
        public void setSendTime(LocalDateTime sendTime) {
            throw new UnsupportedOperationException("cannot change");
        }

        @Override
        public void setDelayDuration(Duration delayDuration) {
            throw new UnsupportedOperationException("cannot change");
        }

        @Override
        public void setExpireTime(LocalDateTime expireTime) {
            throw new UnsupportedOperationException("cannot change");
        }

        @Override
        public void setActualConsumeTime(LocalDateTime actualConsumeTime) {
            this.actualConsumeTime = actualConsumeTime;
        }

        @Override
        public String getDelayMessageCode() {
            return String.valueOf(delayMessage.getMessageId());
        }

        @Override
        public Object getDelayMessageContent() {
            return delayMessage.getMessageContent();
        }

        @Override
        public LocalDateTime getSendTime() {
            return delayMessage.getAddQueueTime();
        }

        @Override
        public Duration getDelayDuration() {
            return Duration.ofMillis(delayMessage.getDelayMillis());
        }

        @Override
        public LocalDateTime getExpireTime() {
            return delayMessage.getAddQueueTime().plus(getDelayDuration());
        }

        @Override
        public LocalDateTime getActualConsumeTime() {
            return actualConsumeTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return delayMessage.getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return delayMessage.compareTo(((WrapperDelayMessage)o).delayMessage);
        }
    }

    @Data
    @AllArgsConstructor
    public static class OMessage{
        private int id;
        private long delayMillis;
    }

    public static final DelayQueue<WrapperDelayMessage> MEM_DELAY_QUEUE = new DelayQueue<>();

    static final Random rand = new Random();

    public static synchronized int getRandomInRange(int min, int max){
        int randNumber = rand.nextInt(max - min + 1) + min;
        return randNumber;
    }

    @Test
    public void test1() throws InterruptedException {
        long[] idMapDelayMillis = new long[]{3000,2000,5000,7000,2000,1000,8000,4000,6000,2000};
        final List<OMessage> l = new ArrayList<>();
        for (int i = 0; i < idMapDelayMillis.length; i++) {
            l.add(new OMessage(i, idMapDelayMillis[i]));
        }
        Collections.shuffle(l);



        //生产者
        for (int i = 0; i < 50; i++) {
            int a = getRandomInRange(0, idMapDelayMillis.length-1);
            WrapperDelayMessage m = new WrapperDelayMessage(new JdkDelayQueue.DelayMessage(l.get(a).getId(), l.get(a).getDelayMillis()));
            new Thread(()->MEM_DELAY_QUEUE.put(m)).start();
        }

        //消费者
        for (int i = 0; i < 3; i++) {
            new Thread(()->{
                while(true) {
                    try {
                        WrapperDelayMessage wrapperDelayMessage = MEM_DELAY_QUEUE.take();
                        wrapperDelayMessage.setActualConsumeTime(LocalDateTime.now());
                        log.info("<===== consume message={}", wrapperDelayMessage.string());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }

        Thread.sleep(600000);

    }
}
