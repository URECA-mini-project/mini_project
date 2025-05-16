package ureca.ureca_mini.winner.kafka;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ureca.ureca_mini.event.EventRepository;
import ureca.ureca_mini.winner.WinnerRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class WinnerServiceTest {
    @Autowired
    private WinnerService winnerService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WinnerRepository winnerRepository;

    @Autowired
    private WinnerCountRepository winnerCountRepository;

    @BeforeEach
    void tearDown() {
        // Redis 값 0으로 초기화
        winnerCountRepository.setZero("winner_count");
    }

    @Test
    public void 한번만응모() {
        winnerService.apply(1, 1);

        long count = winnerRepository.count();

        // 쿠폰 1개가 정상적으로 발급되었는지 검증
        assertThat(count).isEqualTo(1);
    }

    //1000명 응모 테스트
    @Test
    public void 여러번응모() throws InterruptedException {
        //given
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        //when
        for (int i = 0; i<threadCount; i++) {
            executorService.submit(() -> {
                try{
                    winnerService.apply(1, 1);
                }
                finally {
                    countDownLatch.countDown(); //1000부터 줄여나가며 0이 되면 메인 스레드를 대기 상태에서 해제
                }
            });
        }
        countDownLatch.await(); //메인 스레드를 대기 상태로 전환

        Thread.sleep(10000); //카프카 동작이 마칠 때까지 대기

        long count = winnerRepository.count();
        assertThat(count).isEqualTo(100);
    }
}
