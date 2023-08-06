package cn.snow.delay.queue.single.jdk.inter;

import java.time.Duration;
import java.time.LocalDateTime;

public interface IDelayMessage {

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

}
