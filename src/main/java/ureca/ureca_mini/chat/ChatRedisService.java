package ureca.ureca_mini.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String, ChatMessageEntity> redisTemplate; //Redis 메시지

    private static final String CHAT_KEY_PREFIX = "CHAT:";


    public void saveMessage(Integer eventId, ChatMessageEntity message) {
        String redisKey = CHAT_KEY_PREFIX + String.valueOf(eventId);
        redisTemplate.opsForList().rightPush(redisKey, message);

        List<ChatMessageEntity> allMessages = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (allMessages == null) return;

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);

        List<ChatMessageEntity> recentMessages = allMessages.stream()
                .filter(msg -> msg.getTimestamp() != null && msg.getTimestamp().isAfter(cutoff))
                .collect(Collectors.toList());

        redisTemplate.delete(redisKey);

        if (!recentMessages.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(redisKey, recentMessages);
        }
    }


    public List<ChatMessageEntity> getMessageHistory(Integer eventId) {
        String redisKey = CHAT_KEY_PREFIX + String.valueOf(eventId);

        List<ChatMessageEntity> list = redisTemplate.opsForList().range(redisKey, 0, -1);

        return list;
    }


    public void deleteChatHistory(Integer eventId) {

        String redisKey = "CHAT:" + String.valueOf(eventId);

        redisTemplate.delete(redisKey);
    }

}