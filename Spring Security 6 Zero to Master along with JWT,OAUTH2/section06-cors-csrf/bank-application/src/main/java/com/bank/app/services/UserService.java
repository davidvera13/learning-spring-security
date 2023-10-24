package com.bank.app.services;

import com.bank.app.shared.dto.CustomerDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService { // extends UserDetailsService {
    CustomerDto createUser(CustomerDto customerDto);

    List<CustomerDto> findByEmail(String name);
}
