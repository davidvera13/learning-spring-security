package com.example.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for test
 * We must pass:
 * <ul>
 *     <li>user: user</li>
 *     <li>password (example): 63aa414c-27fd-4fb7-8514-c21514601020</li>
 *     <li>password is printed in console </li>
 * </ul>
 *
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
