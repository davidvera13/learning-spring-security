package com.bank.app.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AuthorityDto {
    private Long id;

    private String name;

    private CustomerDto customer;
}
