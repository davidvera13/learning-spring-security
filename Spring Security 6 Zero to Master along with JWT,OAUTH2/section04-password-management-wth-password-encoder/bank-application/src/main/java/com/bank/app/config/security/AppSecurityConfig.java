package com.bank.app.config.security;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

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
        return http.csrf(AbstractHttpConfigurer::disable)
        //return http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact", "/auth/register").permitAll())
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
    ///**
    // * NoOpPasswordEncoder is absolutely not recommended for production
    // * @return a password encoder instance
    // */
    //@Bean
    //PasswordEncoder passwordEncoder() {
    //    return NoOpPasswordEncoder.getInstance();
    //}

    // Adding password encoder
    // Argon2PasswordEncoder > SCryptPasswordEncoder > BCryptPasswordEncoder > PasswordEncoder
    @Bean
    PasswordEncoder encoder() {
        // we can add salt to password  (log round)
        return new BCryptPasswordEncoder();
    }


}
