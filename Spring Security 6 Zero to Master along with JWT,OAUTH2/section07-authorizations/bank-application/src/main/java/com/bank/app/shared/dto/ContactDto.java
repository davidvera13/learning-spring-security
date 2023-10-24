package com.bank.app.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ContactDto {
	private String contactId;
	private String contactName;
	private String contactEmail;
	private String subject;
	private String message;
	private Date createDt;

}
