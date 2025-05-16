package ureca.ureca_mini.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.chat.entity.ChatMessageEntity;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, ChatMessageEntity> redisTemplate;

    public void publish(String topic, ChatMessageEntity message) {
        redisTemplate.convertAndSend(topic, message);
    }
}