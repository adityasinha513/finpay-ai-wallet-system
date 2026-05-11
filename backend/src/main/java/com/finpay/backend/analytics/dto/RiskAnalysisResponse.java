package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RiskAnalysisResponse {

    private Integer riskScore;

    private String riskLevel;

    private String analysis;
}