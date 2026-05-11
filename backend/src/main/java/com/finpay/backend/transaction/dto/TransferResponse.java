package com.finpay.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferResponse {

    private String referenceId;

    private String senderWallet;

    private String receiverWallet;

    private BigDecimal amount;

    private String status;

    private String message;
}