package com.example.oauth2app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppSecurityConfig {
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests)->requests.anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }


    /* We can use application.properties instead to setup
    @Bean
    public ClientRegistrationRepository clientRepository() {
        ClientRegistration clientReg = clientRegistration();
        return new InMemoryClientRegistrationRepository(clientReg);
    }

    /**
     * from github
     * clientId		    43120702b230771ee212
     * client Secret	ac362e07f701f10d69a68f1d1c43645523dacd1f
     * @return client registration
     */
    /*private ClientRegistration clientRegistration() {
		return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId("43120702b230771ee212")
                .clientSecret("ac362e07f701f10d69a68f1d1c43645523dacd1f")
                .build();
	 }*/
}
