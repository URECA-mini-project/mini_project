package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.WinnerEntity;
import ureca.ureca_mini.winner.WinnerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryRedisService {

    private final WinnerRepository winnerRepository;
    private final WinnerCountRedisRepository winnerCountRedisRepository;

    /**
     * redis로 당첨자 수 확인 후 당첨 여분이 남았다면 당첨자 정보에 추가
     * @param request
     */
    public void entryV1(EntryRequest request) {
        int userId = request.getUserId();
        int eventId = request.getEventId();
        String userIdStr = String.valueOf(userId);
        String eventIdStr = String.valueOf(eventId);

        boolean added = winnerCountRedisRepository.checkAndAdd(eventIdStr, userIdStr);
        if (!added) { // 중복 응모인 경우
            log.info("User {}는 이미 응모를 완료했습니다.", userIdStr);
            return;
        }

        long count = winnerCountRedisRepository.increment(eventIdStr);
        if (count > 100) { // 응모가 마감된 경우
            return;
        }

        winnerRepository.save(WinnerEntity.toWinnerEntity(userId, eventId));
    }
}
