package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FinancialSummaryResponse {

    private BigDecimal totalTransferred;

    private BigDecimal totalReceived;

    private Long totalTransactions;

    private BigDecimal highestTransaction;

    private BigDecimal averageTransactionAmount;
}