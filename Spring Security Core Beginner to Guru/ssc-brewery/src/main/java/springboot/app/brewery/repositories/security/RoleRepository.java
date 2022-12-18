package springboot.app.brewery.repositories.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot.app.brewery.domain.security.RoleEntity;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Override
    Optional<RoleEntity> findById(Long aLong);

    Optional<RoleEntity> findByName(String roleName);
}
