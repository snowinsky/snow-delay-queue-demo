/**
 * delay queue的主要有四个主题。
 * delayQueue：一个queue，可以将消息延迟堆积在里边，到时间才推送出去
 * delayMessage：放在delayQueue里边的消息体，这个消息体需要设置这个消息延迟的时间
 * producer：生产者，生成delayMessage并放入queue
 * consumer: 消费者，消费delayQueue里的消息，一般是一个一直活着的服务体，时刻等待着消费延迟消息
 */
package cn.snow.delay.queue.single.jdk.inter;