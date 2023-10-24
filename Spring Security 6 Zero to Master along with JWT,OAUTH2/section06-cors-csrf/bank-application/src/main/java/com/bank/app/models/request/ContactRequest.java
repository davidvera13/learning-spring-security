package com.bank.app.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ContactRequest {
    private String contactId;
    private String contactName;
    private String contactEmail;
    private String subject;
    private String message;
    private Date createDt;

}
