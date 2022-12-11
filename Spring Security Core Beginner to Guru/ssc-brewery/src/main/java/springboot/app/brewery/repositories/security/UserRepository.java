package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.app.brewery.domain.security.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
