package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WinnerCountRedisRepository {

    private static final String WINNER_COUNT_KEY = "ureca:mini2:events:%d-count";
    private static final String WINNERS_KEY = "ureca:mini2:events:%d-winners";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * eventId에 해당하는 이벤트의 당첨자 수 세기
     * @param eventId
     * @return
     */
    public Long increment(int eventId) {
        return redisTemplate.opsForValue().increment(genKey(WINNER_COUNT_KEY, eventId));
    }

    /**
     * 사용자가 이미 응모했는지 확인하고 처음이면 저장
     * @param eventId
     * @param userId
     * @return true면 첫 응모, false면 중복
     */
    public boolean checkAndAdd(String userId, int eventId) {
        String key = genKey(WINNERS_KEY, eventId);
        Long added = redisTemplate.opsForSet().add(key, userId);

        return added != null && added == 1;
    }

    /**
     * 이벤트 삭제
     * @param eventId
     */
    public void deleteByKey(int eventId) {
        redisTemplate.delete(genKey(WINNER_COUNT_KEY, eventId));
        redisTemplate.delete(genKey(WINNERS_KEY, eventId));
    }

    private String genKey(String format, int id) {
        return String.format(format, id);
    }
}
