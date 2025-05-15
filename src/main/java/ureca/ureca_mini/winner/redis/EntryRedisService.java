package ureca.ureca_mini.winner.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ureca.ureca_mini.winner.EntryRequest;
import ureca.ureca_mini.winner.WinnerEntity;
import ureca.ureca_mini.winner.WinnerRepository;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryRedisService {

    private final WinnerRepository winnerRepository;
    private final WinnerRedisRepository winnerRedisRepository;

    /**
     * redis로 당첨자 수 확인 후 당첨 여분이 남았다면 당첨자 정보에 추가
     * @param request
     */
    public boolean entryV1(EntryRequest request) {
        int userId = request.getUserId();
        int eventId = request.getEventId();

        if (!tryEntry(userId, eventId)) { // 응모가 불가능한 경우
            return false;
        }

        // 응모자 100명이 모두 모인 경우에 실제 db에 save하는 로직
        winnerRepository.save(WinnerEntity.toWinnerEntity(userId, eventId));
        return true;
    }

    public boolean entryV2(EntryRequest request) {
        int userId = request.getUserId();
        int eventId = request.getEventId();

        if (!tryEntry(userId, eventId)) { // 응모가 불가능한 경우
            return false;
        }

        return true;
    }

    /**
     * 응모가 가능한지 확인. winner set, winner count 업데이트
     * @param eventId
     * @param userId
     * @return 중복 응모가 아니고 응모 마감이 아니라면 true, 아니면 false
     */
    private boolean tryEntry(int userId, int eventId) {
        String userIdStr = String.valueOf(userId);
        String eventIdStr = String.valueOf(eventId);
        boolean added = winnerRedisRepository.checkAndAdd(userIdStr, eventIdStr); // winner set에 저장
        if (!added) { // 중복 응모인 경우
//            log.info("User {}는 이미 응모를 완료했습니다.", userId);
            return false;
        }

        long count = winnerRedisRepository.increment(eventIdStr); // winner count 업데이트
        if (count == 100) { // 100명이 모집된 경우
            saveToMysql(eventId);
            return true;
        }

        if (count > 100) { // 응모 마감인 경우
            return false;
        }

        return true;
    }

    private void saveToMysql(int eventId) {
        Set<String> users = winnerRedisRepository.getValueRange(eventId, 0, 99);
        System.out.println("users = " + users);
        winnerRepository.saveAll(users.stream().map(userId
                -> WinnerEntity.toWinnerEntity(Integer.parseInt(userId), eventId)).toList());
    }
}
