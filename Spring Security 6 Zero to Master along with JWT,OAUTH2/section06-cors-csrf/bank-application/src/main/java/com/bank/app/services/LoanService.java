package com.bank.app.services;

import com.bank.app.shared.dto.LoanDto;

import java.util.List;

public interface LoanService {
    List<LoanDto> findByCustomerIdOrderByStartDtDesc(long id);
}
