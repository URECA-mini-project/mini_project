package ureca.ureca_mini.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ureca.ureca_mini.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    UserEntity findByUsername(String username);

}
