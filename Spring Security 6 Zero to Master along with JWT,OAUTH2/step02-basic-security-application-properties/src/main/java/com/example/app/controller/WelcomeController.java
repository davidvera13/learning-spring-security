package com.example.app.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for test
 * Note:
 * org.springframework.security.web.access.intercept.AuthorizationFilter:
 * > is the default authorization filter used
 * org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter:
 * > is the default html page for authentication
 * org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
 * > filter that checks authentication extracting username and password
 * > the method Authentication attemptAuthentication uses UsernamePasswordAuthenticationToken class
 * org.springframework.security.authentication.AuthenticationManager: called by UsernamePasswordAuthenticationFilter
 * > provides an authentication manager used by UsernamePasswordAuthenticationFilter : attemptAuthentication
 * > We have 5 implementation of the interface: org.springframework.security.authentication.ProviderManager
 * > ProviderManager tries to react with all AuthenticationManager implementations and check which implementation is successful
 * > org.springframework.security.authentication.dao.DaoAuthenticationProvider is one implementation
 * To retrieve userDetails we have a User Service: DaoAuthentiicationProvider uses:
 * > UserDetailsService: org.springframework.security.core.userdetails.UserDetailsService
 * We can from there get one implementation:
 * > CachingUserDetailsService
 * > InMemoryUserDetailsManager
 * > JdbcDaoImpl
 * > JdbcUserDetailsManager...
 * When using application.properties, the InMemoryUserDetailsManager is called
 * The workflow is the following
 * httpCall
 *  -> Authorization filter
 *      -> DefaultLoginPageGeneratingFilter
 *          -> UsernamePasswordAuthenticationFilter
 *              -> UsernamePasswordAuthenticationToken
 *                  -> ProviderManager
 *                      ->  DaoAuthenticationProvider
 *                          -> InMemoryUserDetailsManager
 *                      -> and back ...
 */
@RestController
public class WelcomeController {


    /**
     * sayHello method
     * @return a string saying hello
     */
    @GetMapping("/welcome")
    public String sayHello() {
        return "Welcome to an secured application...";
    }
}
