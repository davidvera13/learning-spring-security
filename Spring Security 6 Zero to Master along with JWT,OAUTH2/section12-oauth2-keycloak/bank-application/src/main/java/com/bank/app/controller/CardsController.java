package com.bank.app.controller;

import com.bank.app.models.response.CardResponse;
import com.bank.app.services.AccountService;
import com.bank.app.services.CardService;
import com.bank.app.shared.dto.AccountDto;
import com.bank.app.shared.dto.CardDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardsController {
    private final AccountService accountService;
    private final CardService cardService;

    @Autowired
    public CardsController(
            AccountService accountService,
            CardService cardService) {
        this.accountService = accountService;
        this.cardService = cardService;
    }

    @GetMapping("/myCards")
    //public List<CardResponse> getCardDetails(@RequestParam long id) {
    public List<CardResponse> getCardDetails(@RequestParam String email) {
        AccountDto accountDto = accountService.findByEmail(email);
        List<CardDto> cardDtos = cardService.findByCustomerId(accountDto.getCustomerId());
        return new ModelMapper().map(cardDtos, new TypeToken<List<CardResponse>>(){}.getType());
    }

}
