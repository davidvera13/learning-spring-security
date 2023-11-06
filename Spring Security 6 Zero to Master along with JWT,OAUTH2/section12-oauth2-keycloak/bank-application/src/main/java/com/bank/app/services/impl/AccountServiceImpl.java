package com.bank.app.services.impl;

import com.bank.app.io.entity.AccountEntity;
import com.bank.app.io.entity.CustomerEntity;
import com.bank.app.io.repository.AccountRepository;
import com.bank.app.io.repository.CustomerRepository;
import com.bank.app.services.AccountService;
import com.bank.app.shared.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountServiceImpl(
            AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public AccountDto findByCustomerId(long id) {
        AccountEntity accountEntity = accountRepository.findByCustomerId(id);
        return new ModelMapper().map(accountEntity, AccountDto.class);
    }

    @Override
    public AccountDto findByEmail(String email) {
        List<CustomerEntity> storedCustomers = customerRepository.findByEmail(email);
        if (storedCustomers != null && !storedCustomers.isEmpty()) {
            AccountEntity accountEntity = accountRepository.findByCustomerId(storedCustomers.get(0).getId());
            if (accountEntity != null)
                return new ModelMapper().map(accountEntity, AccountDto.class);
        }
        return null;
    }
}