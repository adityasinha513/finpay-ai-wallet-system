package com.finpay.backend.auth.entity;

import com.finpay.backend.auth.enums.KycStatus;
import com.finpay.backend.common.entity.BaseEntity;
import com.finpay.backend.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.finpay.backend.auth.enums.Role;
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String transactionPin;
    private String panNumber;
    private String aadhaarMasked;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus = KycStatus.PENDING;

    @OneToOne(mappedBy = "user")
    private Wallet wallet;
}