package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.AgentRecommendationResponse;
import com.finpay.backend.analytics.dto.AgentSystemResponse;
import com.finpay.backend.analytics.dto.FraudAlertResponse;
import com.finpay.backend.analytics.dto.RiskAnalysisResponse;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiAgentCoordinatorService {

    private final FinancialAgentService
            financialAgentService;

    private final FraudMonitoringAgentService
            fraudMonitoringAgentService;

    private final AnalyticsService
            analyticsService;

    public AgentSystemResponse
    executeAgentWorkflow(
            User user
    ) {

        FraudAlertResponse fraudAnalysis =
                fraudMonitoringAgentService
                        .monitorFraudRisk(
                                user
                        );

        AgentRecommendationResponse
                recommendations =
                financialAgentService
                        .generateFinancialRecommendations(
                                user
                        );

        RiskAnalysisResponse riskAnalysis =
                analyticsService
                        .analyzeFinancialRisk(
                                user
                        );

        String systemStatus =
                "MULTI_AGENT_SYSTEM_ACTIVE";

        return new AgentSystemResponse(
                fraudAnalysis,
                recommendations,
                riskAnalysis,
                systemStatus
        );
    }
}