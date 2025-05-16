package ureca.ureca_mini.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);

    // 종료되지 않은 이벤트 조회
    List<EventEntity> findByEventDateAfter(LocalDateTime dateTime);
    Optional<EventEntity> findById(Integer id);
}
