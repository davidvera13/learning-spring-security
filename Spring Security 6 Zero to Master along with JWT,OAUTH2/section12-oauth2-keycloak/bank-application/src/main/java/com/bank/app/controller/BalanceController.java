package com.bank.app.controller;

import com.bank.app.models.response.AccountTransactionResponse;
import com.bank.app.services.AccountService;
import com.bank.app.services.AccountTransactionService;
import com.bank.app.shared.dto.AccountDto;
import com.bank.app.shared.dto.AccountTransactionDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class BalanceController {
    private final AccountService accountService;
    private final AccountTransactionService accountTransactionService;

    @Autowired
    public BalanceController(
            AccountService accountService,
            AccountTransactionService accountTransactionService) {
        this.accountService = accountService;
        this.accountTransactionService = accountTransactionService;
    }

    @GetMapping("/myBalance")
    public List<AccountTransactionResponse> getBalanceDetails(@RequestParam String email) {
    //public List<AccountTransactionResponse> getBalanceDetails(@RequestParam long id) {
        AccountDto accountDto = accountService.findByEmail(email);

        List<AccountTransactionDto> accountTransactionDtos =
                accountTransactionService.findByCustomerIdOrderByTransactionDtDesc(accountDto.getCustomerId());

        return new ModelMapper().map(accountTransactionDtos, new TypeToken<List<AccountTransactionResponse>>(){}.getType());
    }

}
