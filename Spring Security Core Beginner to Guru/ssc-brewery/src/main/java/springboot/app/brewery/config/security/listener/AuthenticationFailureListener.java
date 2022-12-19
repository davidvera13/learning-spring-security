package springboot.app.brewery.config.security.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import springboot.app.brewery.domain.security.LoginFailureEntity;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.LoginFailureRepository;
import springboot.app.brewery.repositories.security.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFailureListener {
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationFailureListener(LoginFailureRepository loginFailureRepository, UserRepository userRepository) {
        this.loginFailureRepository = loginFailureRepository;
        this.userRepository = userRepository;
    }


    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event){
        log.debug("Login failure");

        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();
            LoginFailureEntity.LoginFailureEntityBuilder builder = LoginFailureEntity.builder();

            if(token.getPrincipal() instanceof String){
                log.debug("Attempted Username: " + token.getPrincipal());
                builder.username((String) token.getPrincipal());
                userRepository.findByUsername((String) token.getPrincipal()).ifPresent(builder::user);
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }
            LoginFailureEntity failure = loginFailureRepository.save(builder.build());
            log.debug("Failure Event: " + failure.getId());

            if (failure.getUser() != null) {
                lockUserAccount(failure.getUser());
            }
        }
    }
    private void lockUserAccount(UserEntity user) {
        List<LoginFailureEntity> failures = loginFailureRepository.findAllByUserAndCreatedDateIsAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if(failures.size() > 3){
            log.debug("Locking User Account... ");
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }

}
