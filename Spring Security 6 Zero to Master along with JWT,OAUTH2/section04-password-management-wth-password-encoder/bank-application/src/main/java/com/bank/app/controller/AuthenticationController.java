package com.bank.app.controller;

import com.bank.app.models.CustomerRequest;
import com.bank.app.models.CustomerResponse;
import com.bank.app.services.UserService;
import com.bank.app.shared.dto.CustomerDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = new ModelMapper();
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> createUser(@RequestBody CustomerRequest customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPwd());
        customer.setPwd(encodedPassword);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        CustomerDto response = userService.createUser(customerDto);
        if(response != null)
            return new ResponseEntity<>(
                    modelMapper.map(response, CustomerResponse.class),
                    HttpStatus.CREATED);
        return ResponseEntity.internalServerError().body("An exception was thrown");
    }
}
