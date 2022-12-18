package springboot.app.brewery.config.annotations.order;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.read') OR " +
        "hasAuthority('customer.order.read') AND " +
        "@beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId )")
public @interface BeerOrderRead {
}
