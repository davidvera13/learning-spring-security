package com.bank.app.services.impl;

import com.bank.app.io.entity.AccountTransactionEntity;
import com.bank.app.io.repository.AccountTransactionsRepository;
import com.bank.app.services.AccountTransactionService;
import com.bank.app.shared.dto.AccountTransactionDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountTransactionServiceImpl implements AccountTransactionService {
    private final AccountTransactionsRepository accountTransactionsRepository;

    @Autowired
    public AccountTransactionServiceImpl(AccountTransactionsRepository accountTransactionsRepository) {
        this.accountTransactionsRepository = accountTransactionsRepository;
    }

    @Override
    public List<AccountTransactionDto> findByCustomerIdOrderByTransactionDtDesc(long id) {
        List<AccountTransactionEntity> accountTransactionEntities =
                accountTransactionsRepository.findByCustomerIdOrderByTransactionDtDesc(id);
        return new ModelMapper()
                .map(accountTransactionEntities, new TypeToken<List<AccountTransactionDto>>() {}.getType());
    }
}
