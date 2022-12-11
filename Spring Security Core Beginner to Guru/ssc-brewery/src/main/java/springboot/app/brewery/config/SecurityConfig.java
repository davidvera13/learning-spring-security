package springboot.app.brewery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// replaced by local refactored class
// import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import springboot.app.brewery.config.security.PasswordEncoderFactories;
import springboot.app.brewery.config.security.RestHeaderAuthFilter;
import springboot.app.brewery.config.security.RestUrlAuthFilter;
//import org.springframework.security.crypto.password.StandardPasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // each filter is set
    private RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager) {
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }
    private RestUrlAuthFilter restUrlAuthFilter(AuthenticationManager authenticationManager) {
        RestUrlAuthFilter filter = new RestUrlAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // NOT RECOMMENDED.... for legacy support only
        // return NoOpPasswordEncoder.getInstance();
        // return new LdapShaPasswordEncoder();
        // return new StandardPasswordEncoder();
        // return new BCryptPasswordEncoder();

        // allow to delegate password encoding to Springboot
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(
                        restHeaderAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        restUrlAuthFilter(authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class);
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
                // .password("{noop}admin") -> noop tag is no longer required, we created the passwordEncoder bean !
                .password("{sha256}400cccfce5a3263bfb77d4ee923a37369dcbcacf82289b27fb17aa696094688e395278d2f27b78c9")
                .roles("ADMIN")
                .and()
                .withUser("me")
                // .password("{noop}me")  -> noop tag is no longer required, we created the passwordEncoder bean !
                .password("{bcrypt}$2a$10$LU1MPf11TWBVzknQ3hEhe.e9paaYvrgWH3xcuJQOpmTggGKbEkuT.") // USING BCRYPT HERE
                .roles("USER")
                .and()
                .withUser("scott")
                // .password("{noop}tiger")  -> noop tag is no longer required, we created the passwordEncoder bean !
                .password("{argon2}$argon2id$v=19$m=4096,t=3,p=1$ElEuOwh4S4EQb46rPQqh8g$y2tVJQZgn/0Z8i4Jq24rs34MxG+0m2KyBPdJl55YtSU") // -> we yse
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
