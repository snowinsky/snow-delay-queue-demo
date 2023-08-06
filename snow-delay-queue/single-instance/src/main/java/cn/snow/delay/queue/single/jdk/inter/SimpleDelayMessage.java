package cn.snow.delay.queue.single.jdk.inter;

import java.time.Duration;
import java.time.LocalDateTime;

public class SimpleDelayMessage implements IDelayMessage {

    private String messageCode;
    private Object messageContent;
    private LocalDateTime sendTime;
    private Duration delayDuration;
    private LocalDateTime expireTime;
    private LocalDateTime actualConsumeTime;


    @Override
    public void setDelayMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    @Override
    public void setDelayMessageContent(Object messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public void setDelayDuration(Duration delayDuration) {
        this.delayDuration = delayDuration;
    }

    @Override
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public void setActualConsumeTime(LocalDateTime actualConsumeTime) {
        this.actualConsumeTime = actualConsumeTime;
    }

    @Override
    public String getDelayMessageCode() {
        return messageCode;
    }

    @Override
    public Object getDelayMessageContent() {
        return messageContent;
    }

    @Override
    public LocalDateTime getSendTime() {
        return sendTime;
    }

    @Override
    public Duration getDelayDuration() {
        return delayDuration;
    }

    @Override
    public LocalDateTime getExpireTime() {
        if (expireTime != null) {
            return expireTime;
        }
        if (delayDuration != null && sendTime != null) {
            return sendTime.plus(delayDuration);
        }
        return expireTime;
    }

    @Override
    public LocalDateTime getActualConsumeTime() {
        return actualConsumeTime;
    }
}
