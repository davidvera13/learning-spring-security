package springboot.app.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Entity
@Table(name = "users")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // other properties can be added ...
    private String password;
    private String username;

//    @Singular
//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "user_authority",
//            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")},
//            inverseJoinColumns = { @JoinColumn(name="authority_id", referencedColumnName = "id")}
//    )
//    private Set<GrantedAuthorityEntity> authorities;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = { @JoinColumn(name="role_id", referencedColumnName = "id")}
    )
    private Set<RoleEntity> roles;

    // tell hibernate that this property is "calculated" and is not a property it the Users table.
    // It is evaluated based on user relation to role and role relation to authorities.
    @Transient
    private Set<GrantedAuthorityEntity> authorities;

    @Builder.Default
    private Boolean accountNonExpired = true;
    @Builder.Default
    private Boolean accountNonLocked = true;
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    @Builder.Default
    private Boolean enabled = true;

    // helper method
    public Set<GrantedAuthorityEntity> getAuthorities() {
        return this.roles.stream()
                .map(RoleEntity::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
