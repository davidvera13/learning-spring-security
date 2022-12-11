package springboot.app.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> {
                    authorize
                            .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll();
                })
                // .authorizeRequests().antMatchers("/).permitAll()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                    .formLogin()
                .and()
                    .httpBasic();
    }

    // elegant way to build in memory users
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // super.configure(auth);
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .and()
                .withUser("me")
                .password("{noop}me")
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{noop}tiger")
                .roles("CUSTOMER");
    }

    // easiest way to build in memory users
    // @Bean
    // @Override
    // protected UserDetailsService userDetailsService() {
    //    UserDetails admin = User.withDefaultPasswordEncoder()
    //             .username("admin")
    //            .password("admin")
    //            .roles("ADMIN")
    //            .build();
    //
    //     UserDetails me = User.withDefaultPasswordEncoder()
    //         .username("me")
    //            .password("me")
    //            .roles("USER")
    //            .build();
    //
    //    return new InMemoryUserDetailsManager(admin, me);
    // }
}
