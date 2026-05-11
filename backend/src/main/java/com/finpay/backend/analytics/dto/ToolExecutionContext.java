package com.finpay.backend.analytics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToolExecutionContext {

    private FinancialSummaryResponse
            financialSummary;

    private RiskAnalysisResponse
            riskAnalysis;

    private TransactionBehaviorResponse
            behaviorAnalysis;
}