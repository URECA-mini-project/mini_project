package ureca.ureca_mini.winner.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.WinnerRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class EntryRedisServiceTest {

    @Autowired
    private EntryRedisService entryRedisService;

    @Autowired
    private WinnerRepository winnerRepository;

    @Autowired
    private WinnerRedisRepository winnerRedisRepository;

    @AfterEach
    public void afterEach() {
        winnerRedisRepository.deleteByKey("1");
        winnerRepository.deleteAll();
    }

    @Test
    @DisplayName("응모를 정상적으로 반영합니다")
    public void apply() {
        boolean success = entryRedisService.entryV1(new EntryRequest(1, 1));
        int count = winnerRepository.countByEventId(1);

        Assertions.assertEquals(true, success);
        Assertions.assertEquals(1, count);
    }

    @Test
    @DisplayName("여러 명 응모 시 당첨자 수 만큼만 저장합니다")
    public void applyManyTime() throws Exception {
        int threadCount = 1000; // 1000개의 응모 요청
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 32개의 스레드 풀

        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 작업이 끝날 때까지 기다리는 동기화 메커니즘. 초기값은 threadCount로 설정
        for (int i=0; i<threadCount; i++) {
            int userId = i;
            executorService.execute(() -> {
                try {
                    entryRedisService.entryV1(new EntryRequest(userId, 1));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // count가 0이되면 실행이 계속됨.

        Thread.sleep(10000);

        int redisCount = winnerRedisRepository.countWinnerByEventId(1);
        int mysqlCount = winnerRepository.countByEventId(1);
        Assertions.assertEquals(threadCount, redisCount);
        Assertions.assertEquals(100, mysqlCount);
    }

    @Test
    @DisplayName("중복 응모는 불가능합니다")
    public void applyDuplicate() {
        //given
        entryRedisService.entryV1(new EntryRequest(1, 1));

        // when
        boolean success = entryRedisService.entryV1(new EntryRequest(1, 1));

        // then
        Assertions.assertFalse(success);
    }

    @Test
    @DisplayName("응모 마감 후 응모는 불가능합니다")
    public void entryAfterClosed() {
        // given
        for (int i=1; i<=100; i++) {
            entryRedisService.entryV1(new EntryRequest(i, 1));
        }

        // when
        boolean success = entryRedisService.entryV1(new EntryRequest(101, 1));

        // then
        Assertions.assertFalse(success);
    }
}
