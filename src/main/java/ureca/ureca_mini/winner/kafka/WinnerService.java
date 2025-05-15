package ureca.ureca_mini.winner.kafka;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WinnerService {
    private final WinnerProducer winnerProducer;
    private final WinnerCountRepository winnerCountRepository;

    // 쿠폰 발급 로직
    //TODO 하드코딩 수정(eventId, userId)
    public boolean apply(int userId, int eventId) {
        //DB에 직접 바로 접근하는 경우 (이 경우 여러개가 한꺼번에 접근하면서 오류 발생)
        //if(winnerRepository.countByEventId(eventId) > 100) return false;

        //redis를 사용한 count
        long count = winnerCountRepository.increment();
        if(count > 100) return false;

        // 발급이 가능한 경우 ->  쿠폰 새로 생성(발급)
        // TODO 이벤트, 유저 id 하드코딩 수정. createdAt 필드 추가 후 save 메소드 파라매터 수정 필요
        //DB에 영속화하는 로직
        //winnerRepository.save(WinnerEntity.toWinnerEntity(1, 1)); // 쿠폰 발급(mysql)

        //해당 엔티티를 영속화 하는 대신에 kafka로 데이터를 전송
        winnerProducer.create(userId);

        return true;
    }
}