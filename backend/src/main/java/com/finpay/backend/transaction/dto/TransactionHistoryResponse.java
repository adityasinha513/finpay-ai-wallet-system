package com.finpay.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionHistoryResponse {

    private String referenceId;

    private String senderWallet;

    private String receiverWallet;

    private BigDecimal amount;

    private String type;

    private String status;

    private String description;

    private LocalDateTime createdAt;
}