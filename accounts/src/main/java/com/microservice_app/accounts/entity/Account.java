package com.microservice_app.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="accounts")
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Account extends BaseEntity {
    @Column(name="customer_id")
    private Long customerId;

    @Id
    @Column(name="account_number")
    private Long accountNumber;

    @Column(name="account_type")
    private String accountType;

    @Column(name="branch_address")
    private String branchAddress;
}
