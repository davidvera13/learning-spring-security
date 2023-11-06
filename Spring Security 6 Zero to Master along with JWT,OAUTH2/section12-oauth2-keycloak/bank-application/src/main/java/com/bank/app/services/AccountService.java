package com.bank.app.services;

import com.bank.app.shared.dto.AccountDto;

public interface AccountService {
    AccountDto findByCustomerId(long id);

    AccountDto findByEmail(String email);
}
