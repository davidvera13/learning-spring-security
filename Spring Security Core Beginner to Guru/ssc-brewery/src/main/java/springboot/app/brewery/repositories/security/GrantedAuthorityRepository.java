package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.app.brewery.domain.security.GrantedAuthorityEntity;

public interface GrantedAuthorityRepository extends JpaRepository<GrantedAuthorityEntity, Long> {
}
