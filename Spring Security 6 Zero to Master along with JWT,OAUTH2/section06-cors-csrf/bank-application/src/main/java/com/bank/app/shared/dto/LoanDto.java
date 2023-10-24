package com.bank.app.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class LoanDto {
	private int loanNumber;
	private int customerId;
	private Date startDt;
	private String loanType;
	private int totalLoan;
	private int amountPaid;
	private int outstandingAmount;
	private String createDt;
}
