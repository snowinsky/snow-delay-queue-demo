package cn.snow.delay.queue.single.jdk.inter;

public interface IProducor {

    void sendDelayMessage(IDelayMessage delayMessage, IConsumer delayMessageConsumer);
}
