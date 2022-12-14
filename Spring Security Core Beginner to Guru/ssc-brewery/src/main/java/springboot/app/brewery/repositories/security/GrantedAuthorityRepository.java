package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.app.brewery.domain.security.GrantedAuthorityEntity;

@Repository
public interface GrantedAuthorityRepository extends JpaRepository<GrantedAuthorityEntity, Long> {
}
