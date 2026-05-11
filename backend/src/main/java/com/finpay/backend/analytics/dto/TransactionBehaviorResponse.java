package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionBehaviorResponse {

    private Long recentTransactionCount;

    private String activityLevel;

    private String behaviorMessage;
}