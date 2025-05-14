package ureca.ureca_mini.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ureca.ureca_mini.chat.entity.ForbiddenwordEntity;

import java.util.List;

public interface ForbiddenWordRepository extends JpaRepository<ForbiddenwordEntity, Long> {
    List<ForbiddenwordEntity> findAll();

}
