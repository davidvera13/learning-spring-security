package com.bank.app.services.impl;

import com.bank.app.io.repository.LoanRepository;
import com.bank.app.services.LoanService;
import com.bank.app.shared.dto.LoanDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;

    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<LoanDto> findByCustomerIdOrderByStartDtDesc(long id) {
        return new ModelMapper().map(
                loanRepository.findByCustomerIdOrderByStartDtDesc(id),
                new TypeToken<List<LoanDto>>(){}.getType());
    }
}
