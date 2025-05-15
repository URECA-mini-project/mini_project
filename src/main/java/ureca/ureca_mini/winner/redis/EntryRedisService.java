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

    private static final int LIMIT = 3000;

    private final WinnerRepository winnerRepository;
    private final WinnerJdbcRepository winnerJdbcRepository;
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

        // 응모자 LIMIT명이 모두 모인 경우에 실제 db에 save하는 로직
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

    public boolean entryV3(EntryRequest request) {
        int userId = request.getUserId();
        int eventId = request.getEventId();

        if (!tryEntryByBulkInsert(userId, eventId)) { // 응모가 불가능한 경우
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
        if (count == LIMIT) { // LIMIT명이 모집된 경우
            saveToMysql(eventId);
            return true;
        }

        if (count > LIMIT) { // 응모 마감인 경우
            return false;
        }

        return true;
    }

    private boolean tryEntryByBulkInsert(int userId, int eventId) {
        String userIdStr = String.valueOf(userId);
        String eventIdStr = String.valueOf(eventId);
        boolean added = winnerRedisRepository.checkAndAdd(userIdStr, eventIdStr); // winner set에 저장
        if (!added) { // 중복 응모인 경우
//            log.info("User {}는 이미 응모를 완료했습니다.", userId);
            return false;
        }

        long count = winnerRedisRepository.increment(eventIdStr); // winner count 업데이트
        if (count == LIMIT) { // LIMIT명이 모집된 경우
//            saveToMysqlByBulkInsert(eventId); // 이벤트 종료 후 mysql에 저장한다는 가정 때문에 비활성화
            return true;
        }

        if (count > LIMIT) { // 응모 마감인 경우
            return false;
        }

        return true;
    }

    private void saveToMysql(int eventId) {
        Set<String> users = winnerRedisRepository.getValueRange(eventId, 0, LIMIT - 1);
        System.out.println("users 수 = " + users.size());
        winnerRepository.saveAll(users.stream().map(userId
                -> WinnerEntity.toWinnerEntity(Integer.parseInt(userId), eventId)).toList());
    }

    // 이벤트 종료 후 mysql에 저장한다는 가정 때문에 비활성화
    private void saveToMysqlByBulkInsert(int eventId) {
        Set<String> users = winnerRedisRepository.getValueRange(eventId, 0, LIMIT - 1);
        winnerJdbcRepository.entryBulkInsert(users, eventId);
    }
}
