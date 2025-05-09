package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WinnerCountRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * eventId에 해당하는 이벤트의 당첨자 수 세기
     * @param eventId
     * @return
     */
    public Long increment(String eventId) {
        return redisTemplate.opsForValue().increment(eventId + "-count");
    }

    /**
     * 사용자가 이미 응모했는지 확인하고 처음이면 저장
     * @param eventId
     * @param userId
     * @return true면 첫 응모, false면 중복
     */
    public boolean checkAndAdd(String eventId, String userId) {
        String key = eventId + "-users";
        Long added = redisTemplate.opsForSet().add(key, userId);

        return added != null && added == 1;
    }

    /**
     * 이벤트 삭제
     * @param eventId
     */
    public void deleteByKey(String eventId) {
        redisTemplate.delete(eventId);
        redisTemplate.delete(eventId + "-users");
    }
}
