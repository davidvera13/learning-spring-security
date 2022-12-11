package springboot.app.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@Entity
@Table(name = "authorities")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class GrantedAuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;
    @ManyToMany(mappedBy = "authorities")
    private Set<UserEntity> users;
}
