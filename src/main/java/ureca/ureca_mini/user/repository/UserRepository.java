package ureca.ureca_mini.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ureca.ureca_mini.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    UserEntity findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
}
