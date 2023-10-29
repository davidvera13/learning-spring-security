package com.bank.app.services.impl;

import com.bank.app.io.entity.CardEntity;
import com.bank.app.io.repository.CardsRepository;
import com.bank.app.services.CardService;
import com.bank.app.shared.dto.CardDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    private final CardsRepository cardsRepository;

    @Autowired
    public CardServiceImpl(CardsRepository cardsRepository) {
        this.cardsRepository = cardsRepository;
    }

    @Override
    public List<CardDto> findByCustomerId(long id) {
        List<CardEntity> cardEntities = cardsRepository.findByCustomerId(id);
        return new ModelMapper().map(cardEntities, new TypeToken<List<CardDto>>(){}.getType());
    }
}
