package com.bank.app.models.response;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ContactResponse {
	private String contactId;
	private String contactName;
	private String contactEmail;
	private String subject;
	private String message;
	private Date createDt;

}
