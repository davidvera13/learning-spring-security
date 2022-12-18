package springboot.app.brewery.config.annotations.order;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('order.read', 'customer.order.read')")
public @interface BeerOrderReadV2 {
}
