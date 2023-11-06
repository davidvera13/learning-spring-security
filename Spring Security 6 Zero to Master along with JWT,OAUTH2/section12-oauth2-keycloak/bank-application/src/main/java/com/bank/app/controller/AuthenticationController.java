package com.bank.app.controller;

import com.bank.app.models.request.CustomerRequest;
import com.bank.app.models.response.CustomerResponse;
import com.bank.app.services.UserService;
import com.bank.app.shared.dto.CustomerDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    //private final PasswordEncoder passwordEncoder;

    //@Autowired
    //public AuthenticationController(
    //        UserService userService,
    //        PasswordEncoder passwordEncoder) {
    //    this.userService = userService;
    //    this.passwordEncoder = passwordEncoder;
    //    this.modelMapper = new ModelMapper();
    //}

    @Autowired
    public AuthenticationController(
            UserService userService) {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    //@PostMapping(
    //        value = "/register",
    //        consumes = MediaType.APPLICATION_JSON_VALUE,
    //        produces = MediaType.APPLICATION_JSON_VALUE
    //)
    //public ResponseEntity<Object> createUser(@RequestBody CustomerRequest customer) {
    //    String encodedPassword = passwordEncoder.encode(customer.getPwd());
    //    customer.setPwd(encodedPassword);
    //    CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
    //    CustomerDto response = userService.createUser(customerDto);
    //    if(response != null)
    //        return new ResponseEntity<>(
    //                modelMapper.map(response, CustomerResponse.class),
    //                HttpStatus.CREATED);
    //    return ResponseEntity.internalServerError().body("An exception was thrown");
    //}

    // john@doe.com abc123
    @RequestMapping("/users")
    public CustomerResponse getUserDetailsAfterLogin(Authentication authentication) {
        List<CustomerDto> customerDto = userService.findByEmail(authentication.getName());
        List<CustomerResponse> returnValue = modelMapper.map(customerDto, new TypeToken<List<CustomerResponse>>() {}.getType());
        if (!returnValue.isEmpty()) {
            return returnValue.get(0);
        } else {
            return null;
        }
    }

}
