package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.app.brewery.domain.security.LoginFailureEntity;
import springboot.app.brewery.domain.security.UserEntity;

import java.sql.Timestamp;
import java.util.List;

public interface LoginFailureRepository extends JpaRepository<LoginFailureEntity, Long> {
    List<LoginFailureEntity> findAllByUserAndCreatedDateIsAfter(UserEntity user, Timestamp timestamp);

}
