package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgentSystemResponse {

    private FraudAlertResponse fraudAnalysis;

    private AgentRecommendationResponse recommendations;

    private RiskAnalysisResponse riskAnalysis;

    private String systemStatus;
}