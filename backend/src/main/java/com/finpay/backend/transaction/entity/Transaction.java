package com.finpay.backend.transaction.entity;

import com.finpay.backend.common.entity.BaseEntity;
import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;
import com.finpay.backend.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {

    @Column(
            nullable = false,
            unique = true
    )
    private String referenceId;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet senderWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet receiverWallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;
}