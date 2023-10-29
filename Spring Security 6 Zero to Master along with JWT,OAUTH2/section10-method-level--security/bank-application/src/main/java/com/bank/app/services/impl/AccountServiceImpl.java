package com.bank.app.services.impl;

import com.bank.app.io.entity.AccountEntity;
import com.bank.app.io.repository.AccountRepository;
import com.bank.app.services.AccountService;
import com.bank.app.shared.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountDto findByCustomerId(long id) {
        AccountEntity accountEntity = accountRepository.findByCustomerId(id);
        return new ModelMapper().map(accountEntity, AccountDto.class);
    }
}
