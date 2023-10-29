package com.bank.app.shared.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AccountDto {
    private int customerId;
    private long accountNumber;
    private String accountType;
    private String branchAddress;
    private String createDt;
}
