package cn.snow.delay.queue.single.jdk.inter;

public class SimpleProducor implements IProducor{
    @Override
    public void sendDelayMessage(IDelayMessage delayMessage, IConsumer delayMessageConsumer) {
        delayMessageConsumer.accept(delayMessage);
    }
}
