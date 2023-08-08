package cn.snow.delay.queue.single.jdk.scheduled_threadpool;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static java.lang.System.out;

public class ScheduledThreadPoolTest {

    private static final ScheduledExecutorService DELAY_RUN_POOL = searchDelayExecutor();
    private static final ExecutorService COMMON_POOL = restoreJobExecutor();

    public static void main(String[] args) {

        Runnable delayTask = () -> out.println("delay do it=" + LocalDateTime.now());
        Runnable runTask = () -> out.println("do it now=" + LocalDateTime.now());

        out.println("now=" + LocalDateTime.now());
        DELAY_RUN_POOL.schedule(delayTask, 2000L, TimeUnit.MILLISECONDS);
        COMMON_POOL.submit(runTask);

        DELAY_RUN_POOL.shutdown();
        COMMON_POOL.shutdown();

    }

    public static ScheduledExecutorService searchDelayExecutor() {
        ScheduledThreadPoolExecutor searchDelayPool = new ScheduledThreadPoolExecutor(
                4
                , new ThreadFactoryBuilder().setNameFormat("delay-query-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                if (getActiveCount() >= 5 * 10) {
                    super.getRejectedExecutionHandler().rejectedExecution(command, this);
                }
                return super.schedule(command, delay, unit);
            }
        };
        searchDelayPool.setMaximumPoolSize(10);
        return searchDelayPool;
    }

    public static ExecutorService restoreJobExecutor() {
        return new ThreadPoolExecutor(
                4
                , 10
                , 0L
                , TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>(1024)
                , new ThreadFactoryBuilder().setDaemon(true).setNameFormat("restore-job-pool-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
