package springboot.app.brewery.domain.security;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.sql.Timestamp;


@Builder
@Entity
@Table(name="login_success")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class LoginSuccessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @ManyToOne
    private UserEntity user;

    private String sourceIp;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;
}
