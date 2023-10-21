package com.bank.app.config.security;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//import javax.sql.DataSource;

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

    /**
     * we want to customize users ...
     * let's consider we want customer table instead
     * Approach 4: we have ambiguity with authenntication manager ,
     * we should remove UserDetailsService bean, because we already implemented it in service layer
     */

    /**
     * Approach 3: Usine database
     */
    //@Bean
    //public UserDetailsService userDetailsService(DataSource dataSource) {
    //    return new JdbcUserDetailsManager(dataSource);
    //}

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }
    /**
     * NoOpPasswordEncoder is absolutely not recommended for production
     * @return a password encoder instance
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Approach 2: create in memory user and defining password encoder through a bean instance
     */
     //@Bean
    //InMemoryUserDetailsManager userDetailsService() {
    //     InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    //     UserDetails admin = User.withUsername("admin")
    //            .password("12345")
    //            .authorities("admin")
    //           .build();
    //
    //       UserDetails user = User.withUsername("user")
    //           .password("12345")
    //           .authorities("read")
    //           .build();
    //   inMemoryUserDetailsManager.createUser(admin);
    //   inMemoryUserDetailsManager.createUser(user);
    //return inMemoryUserDetailsManager;
    // }

    ///**
    // * NoOpPasswordEncoder is absolutely not recommended for production
    // * @return a password encoder instance
    //*/
    //@Bean
    //PasswordEncoder passwordEncoder() {
    //   return NoOpPasswordEncoder.getInstance();
    //}

    ///**
    // * Approach 1 : create in memory user with InMemoryUserDetailsManager
    //* withDefaultPasswordEncoder is unsecure and deprecated. Should not be used in production
    //* @return Stored users in memory
    //*/
    // @Bean
    // InMemoryUserDetailsManager userDetailsService() {
    //     UserDetails admin = User.withDefaultPasswordEncoder()
    //             .username("admin")
    //            .password("12345")
    //            .authorities("admin")
    //            .build();
    //
    //      UserDetails user = User.withDefaultPasswordEncoder()
    //          .username("user")
    //          .password("12345")
    //          .authorities("read")
    //          .build();
    //  return new InMemoryUserDetailsManager(admin, user);
    // }
}
