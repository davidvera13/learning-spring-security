package com.bank.app.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AppSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }

    /**
     *  Configuration to deny all the requests
     */
     // @Bean
     // SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
     //    return http.authorizeHttpRequests((requests) -> requests.anyRequest().denyAll())
     //             .formLogin(withDefaults())
     //             .httpBasic(withDefaults())
     //             .build();
     // }

    /**
     *  Configuration to permit all the requests
     */
    // @Bean
    // SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    //    return http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
    //             .formLogin(withDefaults())
    //             .httpBasic(withDefaults())
    //             .build();
    // }

}
