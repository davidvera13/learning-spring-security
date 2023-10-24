package com.bank.app.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AccountTransactionDto {
    private String transactionId;
    private long accountNumber;
    private int customerId;
    private Date transactionDt;
    private String transactionSummary;
    private String transactionType;
    private int transactionAmt;
    private int closingBalance;
    private String createDt;
}
