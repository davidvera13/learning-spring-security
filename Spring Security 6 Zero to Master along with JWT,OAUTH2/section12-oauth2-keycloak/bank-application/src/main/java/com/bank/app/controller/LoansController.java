package com.bank.app.controller;

import com.bank.app.models.response.LoanResponse;
import com.bank.app.services.AccountService;
import com.bank.app.services.LoanService;
import com.bank.app.shared.dto.AccountDto;
import com.bank.app.shared.dto.LoanDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoansController {
    private final AccountService accountService;
    private final LoanService loanService;

    @Autowired
    public LoansController(
            AccountService accountService,
            LoanService loanService) {
        this.accountService = accountService;
        this.loanService = loanService;
    }

    @PostAuthorize("hasRole('USER')")
    @GetMapping("/myLoans")
    //public List<LoanResponse> getLoanDetails(@RequestParam long id) {
    public List<LoanResponse> getLoanDetails(@RequestParam String email) {
        AccountDto accountDto = accountService.findByEmail(email);
        List<LoanDto> loanDtos = loanService.findByCustomerIdOrderByStartDtDesc(accountDto.getCustomerId());
        return new ModelMapper().map(loanDtos, new TypeToken<List<LoanResponse>>(){}.getType());
    }

}