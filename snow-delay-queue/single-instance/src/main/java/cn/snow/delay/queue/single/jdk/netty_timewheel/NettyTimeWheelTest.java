package cn.snow.delay.queue.single.jdk.netty_timewheel;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

public class NettyTimeWheelTest {

    public static final HashedWheelTimer WHEEL_TIMER = new HashedWheelTimer(1, TimeUnit.SECONDS, 60);

    public static void main(String[] args) {
        System.out.println("now=" + LocalDateTime.now());
        WHEEL_TIMER.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("do it delay until="+ LocalDateTime.now());
            }
        }, 2000L, TimeUnit.MILLISECONDS);
    }
}
