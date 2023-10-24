package com.bank.app.controller;

import com.bank.app.models.response.AccountTransactionResponse;
import com.bank.app.services.AccountTransactionService;
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
    private final AccountTransactionService accountTransactionService;

    @Autowired
    public BalanceController(AccountTransactionService accountTransactionService) {
        this.accountTransactionService = accountTransactionService;
    }

    @GetMapping("/myBalance")
    public List<AccountTransactionResponse> getBalanceDetails(@RequestParam long id) {
        List<AccountTransactionDto> accountTransactionDtos =
                accountTransactionService.findByCustomerIdOrderByTransactionDtDesc(id);
        return new ModelMapper().map(accountTransactionDtos, new TypeToken<List<AccountTransactionResponse>>(){}.getType());
    }

}
