package springboot.app.brewery.config.annotations.customer;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// the annotation should be retained at runtime, so that reflection can be done at this moment
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('brewery.create')")
public @interface CustomerCreatePermission {
}
