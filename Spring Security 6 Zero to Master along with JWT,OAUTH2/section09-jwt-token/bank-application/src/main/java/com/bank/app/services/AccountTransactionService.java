package com.bank.app.services;

import com.bank.app.shared.dto.AccountTransactionDto;

import java.util.List;

public interface AccountTransactionService {
    List<AccountTransactionDto> findByCustomerIdOrderByTransactionDtDesc(long id);
}
