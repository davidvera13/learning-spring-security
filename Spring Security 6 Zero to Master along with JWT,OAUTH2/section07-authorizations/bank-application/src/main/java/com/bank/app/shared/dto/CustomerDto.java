package com.bank.app.shared.dto;

import com.bank.app.io.entity.AuthorityEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String pwd;
    private String role;
    private Date createDt;
    private Set<AuthorityEntity> authorities;
}
