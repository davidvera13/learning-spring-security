package com.bank.app.models.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CustomerResponse {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String role;
    private Date createDt;
}
