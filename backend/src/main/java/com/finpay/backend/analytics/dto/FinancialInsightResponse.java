package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FinancialInsightResponse {

    private String insightType;

    private String message;

    private String severity;
}