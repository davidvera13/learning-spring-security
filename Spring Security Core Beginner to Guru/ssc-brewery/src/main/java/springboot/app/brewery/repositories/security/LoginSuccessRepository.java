package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.app.brewery.domain.security.LoginSuccessEntity;

public interface LoginSuccessRepository extends JpaRepository<LoginSuccessEntity, Long> {
}