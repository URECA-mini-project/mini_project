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
     * 이벤트 삭제
     * @param eventId
     */
    public void deleteByKey(String eventId) {
        redisTemplate.delete(eventId);
    }
}
