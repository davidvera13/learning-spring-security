package com.example.oauth2app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecureController {
    @GetMapping("/")
    public String main(OAuth2AuthenticationToken token) throws JsonProcessingException {
        // System.out.println(token.getPrincipal());
        ObjectMapper mapper = new ObjectMapper();
        String tokenStr = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(token);
        // print in a pretty way the whole object
        System.out.println(tokenStr);
        return "secure.html";
    }
}
