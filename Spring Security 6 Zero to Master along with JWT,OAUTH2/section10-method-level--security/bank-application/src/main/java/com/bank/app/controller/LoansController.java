package com.bank.app.controller;

import com.bank.app.models.response.LoanResponse;
import com.bank.app.services.LoanService;
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
    private final LoanService loanService;

    @Autowired
    public LoansController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostAuthorize("hasRole('USER')")
    @GetMapping("/myLoans")
    public List<LoanResponse> getLoanDetails(@RequestParam long id) {
        List<LoanDto> loanDtos = loanService.findByCustomerIdOrderByStartDtDesc(id);
        return new ModelMapper().map(loanDtos, new TypeToken<List<LoanResponse>>(){}.getType());
    }

}