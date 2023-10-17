package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for test
 * We configured application.properties to contain username & passord
 * Not recommended for production
 * spring.security.user.name=root
 * spring.security.user.password=rootpass
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
