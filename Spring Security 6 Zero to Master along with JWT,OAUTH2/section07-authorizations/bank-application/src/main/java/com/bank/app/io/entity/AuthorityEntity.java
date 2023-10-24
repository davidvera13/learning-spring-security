package com.bank.app.io.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // many authorities to one customer
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
