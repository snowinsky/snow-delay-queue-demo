package cn.snow.delay.queue.single.jdk.inter;

import java.time.Duration;
import java.time.LocalDateTime;

public interface IDelayMessage {

    default IDelayMessage delayMessage(String messageCode, Object messageContent, Duration delayDuration) {
        setDelayMessageCode(messageCode);
        setDelayMessageContent(messageContent);
        setDelayDuration(delayDuration);
        setSendTime(LocalDateTime.now());
        setExpireTime(getSendTime().plus(delayDuration));
        setActualConsumeTime(null);
        return this;
    }

    void setDelayMessageCode(String messageCode);

    void setDelayMessageContent(Object messageContent);

    void setSendTime(LocalDateTime sendTime);

    void setDelayDuration(Duration delayDuration);

    void setExpireTime(LocalDateTime expireTime);

    void setActualConsumeTime(LocalDateTime actualConsumeTime);

    String getDelayMessageCode();

    Object getDelayMessageContent();

    LocalDateTime getSendTime();

    Duration getDelayDuration();

    LocalDateTime getExpireTime();

    LocalDateTime getActualConsumeTime();

    default String string(){
        return "messageCode=" + getDelayMessageCode() + "\n" +
                "sendTime=" + getSendTime() + "\n" +
                "delay=" + getDelayDuration() + "\n" +
                "expectConsumeTime=" + getExpireTime() + "\n" +
                "actualConsumeTime=" + getActualConsumeTime() + "\n";
    }

}
