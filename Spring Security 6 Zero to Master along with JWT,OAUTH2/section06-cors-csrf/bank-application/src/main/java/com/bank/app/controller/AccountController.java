package com.bank.app.controller;

import com.bank.app.models.response.AccountResponse;
import com.bank.app.services.AccountService;
import com.bank.app.shared.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/myAccount")
    public AccountResponse getAccountDetails(@RequestParam long id) {
        // return "Here are the account details from the DB";
        AccountDto accountDto = accountService.findByCustomerId(id);
        return new ModelMapper().map(accountDto, AccountResponse.class);
    }

}
