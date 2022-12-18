package springboot.app.brewery.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.app.brewery.domain.security.GrantedAuthorityEntity;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.UserRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // failed to lazily initialize a collection of role: springboot.app.brewery.domain.security.UserEntity.authorities,
    // could not initialize proxy - no Session
    // solution 1: adding transactional to the loadUserByUsername method
    // solution 2: make use fetch authorization eager
     @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Getting user info via JPA");

        // we do not need to create a User from spring security core,
         // UserEntity implements UserDetails and CredentialContainer => no type conversion required
         // UserEntity userEntity = userRepository...
         return userRepository
                 .findByUsername(username)
                 .orElseThrow(() -> new UsernameNotFoundException("User " + username + "not found"));
        // return new User(
        //        userEntity.getUsername(),
        //        userEntity.getPassword(),
        //        userEntity.getEnabled(),
        //        userEntity.getAccountNonExpired(),
        //        userEntity.getCredentialsNonExpired(),
        //        userEntity.getAccountNonLocked(),
        //        getAuthorities(userEntity.getAuthorities()));
    }

    // this is not required anymore
    // private Collection<? extends GrantedAuthority> getAuthorities(Set<GrantedAuthorityEntity> authorities) {
    //     if( authorities != null && authorities.size() > 0) {
    //        return authorities.stream()
    //                 .map(GrantedAuthorityEntity::getPermission)
    //                .map(SimpleGrantedAuthority::new)
    //                .collect(Collectors.toSet());
    //    }
    //     return new HashSet<>();
    // }
}
