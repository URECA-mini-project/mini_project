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
    public void entry(EntryRequest request) {
        long count = winnerCountRedisRepository.increment(String.valueOf(request.getEventId()));

        if (count > 100) {
            return;
        }

        winnerRepository.save(WinnerEntity.toWinnerEntity(request.getUserId(), request.getEventId()));
    }
}
