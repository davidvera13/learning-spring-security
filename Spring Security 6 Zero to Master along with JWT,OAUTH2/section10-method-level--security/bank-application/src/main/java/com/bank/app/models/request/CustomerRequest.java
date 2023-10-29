package com.bank.app.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CustomerRequest {
    private String name;
    private String email;
    private String mobileNumber;
    private String pwd;
    private String role;
}
