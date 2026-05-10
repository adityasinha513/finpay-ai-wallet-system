package com.finpay.backend.wallet.entity;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.common.entity.BaseEntity;
import com.finpay.backend.wallet.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String walletNumber;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(nullable = false)
    private BigDecimal dailyTransferLimit =
            new BigDecimal("100000");

    @Column(nullable = false)
    private BigDecimal dailyTransferredAmount =
            BigDecimal.ZERO;

    /**
     * Optimistic lock version for safe concurrent balance updates.
     * Hibernate maps this to a {@code version} column.
     */
    @Version
    private Long version;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}