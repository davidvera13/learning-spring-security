package com.bank.app.services;

import com.bank.app.shared.dto.CardDto;

import java.util.List;

public interface CardService {
    List<CardDto> findByCustomerId(long id);
}
