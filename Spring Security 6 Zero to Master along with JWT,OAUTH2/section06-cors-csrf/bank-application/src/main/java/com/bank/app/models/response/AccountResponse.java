package com.bank.app.models.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AccountResponse {
    private int customerId;
    private long accountNumber;
    private String accountType;
    private String branchAddress;
    private String createDt;
}
