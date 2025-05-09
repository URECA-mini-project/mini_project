package ureca.ureca_mini.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findById(int id);
}
