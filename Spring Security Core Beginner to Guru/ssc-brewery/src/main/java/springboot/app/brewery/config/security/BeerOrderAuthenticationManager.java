package springboot.app.brewery.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import springboot.app.brewery.domain.security.UserEntity;

import java.util.UUID;

@Slf4j
@Component
public class BeerOrderAuthenticationManager {
    public boolean customerIdMatches(Authentication authentication,
                                     UUID customerId) {
        // getting userPrincipal throught authentication object
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        log.debug("Auth user customer id " + authenticatedUser.getCustomer().getId() + " Customer id! " + customerId);
        return authenticatedUser.getCustomer().getId().equals(customerId);
    }
}
