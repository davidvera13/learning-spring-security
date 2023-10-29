package com.bank.app.config.security;

import com.bank.app.config.security.filters.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AppSecurityConfig {

    private final Environment env;

    @Autowired
    public AppSecurityConfig(Environment env) {
        this.env = env;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                // previously when we were trying to access the backend through the UI. We tell spring security to create
                // the JSESSIONID after initial login is completed and to send it to the front end application. The front
                // end application can leverage the same id for all request that will be sent to backend
                .securityContext((context) -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                // we can create a custom csrf implementation instead... it's not a good thing to have it disabled
                //.csrf(AbstractHttpConfigurer::disable)
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers("/auth/register")
                        // CookieCsrfTokenRepository: that persists the CSRF token in a cookie named
                        // "XSRF-TOKEN" and reads from the header "X-XSRF-TOKEN" following the conventions of
                        // AngularJS. When using with AngularJS be sure to use withHttpOnlyFalse().
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // adding filters
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidatorFilter(), BasicAuthenticationFilter.class)
                //return http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        // we can give roles (group of authorities):
                        // ROLE_USER should not start with ROLE_ since ROLE_ is automatically prepended when using hasRole...
                        .requestMatchers("/myAccount").hasRole("USER")
                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/myLoans").hasRole("USER")
                        .requestMatchers("/myCards").hasRole("USER")
                        // we can give specific authorizations on earch entry point
                        // actually, we don't have a complex requirement
                        //.requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
                        //.requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
                        //.requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
                        //.requestMatchers("/myCards").hasAuthority("VIEWCARDS")
                        // returns a 403 error as we don't provide this authority to user...
                        //.requestMatchers("/myCards").hasAuthority("VIEWCARDSDETAILS")
                        //.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/contact", "/auth/users").authenticated()
                        // for remaining routes, we don't care about authority, we consider only users are authenticated
                        .requestMatchers( "/contact", "/auth/users").authenticated()
                        .requestMatchers("/notices", "/auth/register").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    // Argon2PasswordEncoder > SCryptPasswordEncoder > BCryptPasswordEncoder > PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder() {
        // we can add salt to password  (log round)
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        //corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        //corsConfiguration.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
    public CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler() {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        return requestHandler;
    }
}
