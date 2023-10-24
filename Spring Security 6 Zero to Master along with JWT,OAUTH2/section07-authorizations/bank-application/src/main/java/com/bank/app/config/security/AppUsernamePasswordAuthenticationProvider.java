package com.bank.app.config.security;

import com.bank.app.io.entity.AuthorityEntity;
import com.bank.app.io.entity.CustomerEntity;
import com.bank.app.io.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AppUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUsernamePasswordAuthenticationProvider(
            CustomerRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(
            Authentication authentication) throws AuthenticationException {
        // fetch data from db
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();;
        List<CustomerEntity> customerEntities = repository.findByEmail(username);
        if(!customerEntities.isEmpty())
            if(passwordEncoder.matches(password, customerEntities.get(0).getPwd())) {
                //List<GrantedAuthority> authorities = new ArrayList<>();
                //authorities.add(new SimpleGrantedAuthority(customerEntities.get(0).getRole()));
                return new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        getAuthorities(customerEntities.get(0).getAuthorities()));
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        else
            throw new BadCredentialsException("No user registered with this details!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    // helper method
    private List<GrantedAuthority> getAuthorities(Set<AuthorityEntity> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        authorities.forEach(auth -> grantedAuthorities.add(new SimpleGrantedAuthority(auth.getName())));
        return grantedAuthorities;
    }
}
