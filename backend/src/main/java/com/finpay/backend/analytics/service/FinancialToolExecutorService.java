package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.AgentToolResponse;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialToolExecutorService {

    private final AnalyticsService
            analyticsService;

    private final FinancialAgentService
            financialAgentService;

    public AgentToolResponse
    executeTool(
            String toolName,
            User user
    ) {

        return switch (toolName) {

            case "financial_summary" ->

                    new AgentToolResponse(
                            toolName,
                            analyticsService
                                    .getFinancialSummary(
                                            user
                                    )
                    );

            case "risk_analysis" ->

                    new AgentToolResponse(
                            toolName,
                            analyticsService
                                    .analyzeFinancialRisk(
                                            user
                                    )
                    );

            case "behavior_analysis" ->

                    new AgentToolResponse(
                            toolName,
                            analyticsService
                                    .analyzeTransactionBehavior(
                                            user
                                    )
                    );

            case "recommendations" ->

                    new AgentToolResponse(
                            toolName,
                            financialAgentService
                                    .generateFinancialRecommendations(
                                            user
                                    )
                    );

            default ->

                    new AgentToolResponse(
                            "unknown_tool",
                            "Requested tool is not supported."
                    );
        };
    }
}