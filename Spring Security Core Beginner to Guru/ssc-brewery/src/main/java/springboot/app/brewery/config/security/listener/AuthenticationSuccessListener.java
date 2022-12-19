package springboot.app.brewery.config.security.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import springboot.app.brewery.domain.security.LoginSuccessEntity;
import springboot.app.brewery.domain.security.UserEntity;
import springboot.app.brewery.repositories.security.LoginSuccessRepository;

@Slf4j
@Component
public class AuthenticationSuccessListener {

    private final LoginSuccessRepository loginSuccessRepository;

    @Autowired
    public AuthenticationSuccessListener(LoginSuccessRepository loginSuccessRepository) {
        this.loginSuccessRepository = loginSuccessRepository;
    }

    @EventListener
    public void listen(AuthenticationSuccessEvent event){
        log.debug("User Logged In Okay");

        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
            LoginSuccessEntity.LoginSuccessEntityBuilder builder = LoginSuccessEntity.builder();
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if(token.getPrincipal() instanceof User){
                UserEntity user = (UserEntity) token.getPrincipal();
                builder.user(user);
                log.debug("User name logged in: " + user.getUsername() );
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }
            LoginSuccessEntity loginSuccess = loginSuccessRepository.save(builder.build());
            log.debug("Login Success saved. Id: " + loginSuccess.getId());
        }
    }
}