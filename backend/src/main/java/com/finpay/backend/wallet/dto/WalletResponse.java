package com.finpay.backend.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private String walletNumber;

    private BigDecimal balance;

    private String currency;

    private String status;

    private BigDecimal dailyTransferLimit;

    private BigDecimal dailyTransferredAmount;

    private String kycStatus;
}