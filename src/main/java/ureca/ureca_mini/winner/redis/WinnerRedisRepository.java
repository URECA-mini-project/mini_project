package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class WinnerRedisRepository {

    private static final String WINNER_COUNT_KEY = "ureca:mini2:events:%s-count";
    private static final String WINNERS_KEY = "ureca:mini2:events:%s-winners";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * eventId에 해당하는 이벤트의 당첨자 수 세기
     * @param eventId
     * @return
     */
    public Long increment(String eventId) {
        return redisTemplate.opsForValue().increment(genKey(WINNER_COUNT_KEY, eventId));
    }

    /**
     * 사용자가 이미 응모했는지 확인하고 처음이면 저장
     * @param eventId
     * @param userId
     * @return true면 첫 응모, false면 중복
     */
    public boolean checkAndAdd(String userId, String eventId) {
        String key = genKey(WINNERS_KEY, eventId);
        double score = (double) System.currentTimeMillis(); // 현재 시간 (epoch ms)

        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, userId, score));
    }

    /**
     * 이벤트 삭제
     * @param eventId
     */
    public void deleteByKey(String eventId) {
        redisTemplate.delete(genKey(WINNER_COUNT_KEY, eventId));
        redisTemplate.delete(genKey(WINNERS_KEY, eventId));
    }

    public int countWinnerByEventId(String eventId) {
        String key = genKey(WINNER_COUNT_KEY, eventId);
        String count = redisTemplate.opsForValue().get(key);

        return count != null ? Integer.parseInt(count) : 0;
    }

    public int countWinnerByEventId(int eventId) {
        String key = genKey(WINNER_COUNT_KEY, eventId);
        String count = redisTemplate.opsForValue().get(key);

        return count != null ? Integer.parseInt(count) : 0;
    }

    private String genKey(String format, String eventId) {
        return String.format(format, eventId);
    }

    private String genKey(String format, int eventId) {
        return String.format(format, eventId);
    }

    public Set<String> getValue(int eventId) {
        return redisTemplate.opsForSet().members(genKey(WINNERS_KEY, eventId));
    }

    public Set<String> getValueRange(int eventId, int start, int end) {
        String key = genKey(WINNERS_KEY, eventId);
        return redisTemplate.opsForZSet().range(key, start, end);
    }
}
