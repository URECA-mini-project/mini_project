package ureca.ureca_mini.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.chat.entity.ChatMessageEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRedisService {

    private final RedisTemplate<String, ChatMessageEntity> redisTemplate; //Redis 메시지
    private final RedisTemplate<String, String> stringRedisTemplate;    //도배 추적

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

    // 유저 메시지 타임스탬프 기록
    public void recordUserMessageTime(String userId) {
        String key = "SPAM:" + userId;
        stringRedisTemplate.opsForList().rightPush(key, String.valueOf(System.currentTimeMillis()));
        stringRedisTemplate.expire(key, Duration.ofSeconds(10)); // 10초 유지
    }

    // 도배 판단
    public boolean isSpam(String userId, int limit, int seconds) {
        String key = "SPAM:" + userId;
        List<String> timestamps = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (timestamps == null) return false;

        long now = System.currentTimeMillis();
        long windowStart = now - (seconds * 1000L);

        long count = timestamps.stream()
                .mapToLong(Long::parseLong)
                .filter(t -> t >= windowStart)
                .count();

        return count >= limit;
    }

}