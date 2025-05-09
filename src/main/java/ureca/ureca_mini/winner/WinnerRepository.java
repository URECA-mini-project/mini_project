package ureca.ureca_mini.winner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerRepository extends JpaRepository<WinnerEntity, Integer> {
    int countByEventId(int eventId);
}
