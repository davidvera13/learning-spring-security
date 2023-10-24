package com.bank.app.controller;

import com.bank.app.models.response.CardResponse;
import com.bank.app.services.CardService;
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
    private final CardService cardService;

    @Autowired
    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/myCards")
    public List<CardResponse> getCardDetails(@RequestParam long id) {
        List<CardDto> cardDtos = cardService.findByCustomerId(id);
        return new ModelMapper().map(cardDtos, new TypeToken<List<CardResponse>>(){}.getType());
    }

}
