package ureca.ureca_mini.winner.kafka;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository //DAO
public class WinnerCountRepository {

    //redis에 접속하고 데이터를 읽고 쓸 수 있도록 제공하는 스프링 탬플릿
    private final RedisTemplate<String, String> redisTemplate;

    public WinnerCountRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Long increment() {
        return redisTemplate.opsForValue() //Redis에서 String 타입 값을 다룰 때 사용하는 API
                .increment("winner_count"); //레디스 키 +1
    }

    //key 삭제
    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }

    //key value 0으로 만들기
    public void setZero(String key) {
        redisTemplate.opsForValue().set(key, "0");
    }
}