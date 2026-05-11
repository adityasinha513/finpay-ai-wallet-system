package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.*;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialAgentService {

    private final AnalyticsService analyticsService;

    public AgentRecommendationResponse
    generateFinancialRecommendations(
            User user
    ) {

        FinancialSummaryResponse summary =
                analyticsService
                        .getFinancialSummary(user);

        RiskAnalysisResponse risk =
                analyticsService
                        .analyzeFinancialRisk(user);

        TransactionBehaviorResponse behavior =
                analyticsService
                        .analyzeTransactionBehavior(user);

        List<FinancialInsightResponse> insights =
                analyticsService
                        .getFinancialInsights(user);

        List<String> recommendations =
                new ArrayList<>();

        if ("HIGH".equals(risk.getRiskLevel())) {

            recommendations.add(
                    "Monitor recent transactions carefully due to elevated financial risk."
            );
        }

        if ("HIGH".equals(
                behavior.getActivityLevel()
        )) {

            recommendations.add(
                    "Your recent transaction frequency is unusually high."
            );
        }

        if (summary.getTotalTransferred()
                .compareTo(summary.getTotalReceived()) > 0) {

            recommendations.add(
                    "Your outgoing transfers exceed incoming funds."
            );
        }

        for (FinancialInsightResponse insight
                : insights) {

            recommendations.add(
                    insight.getMessage()
            );
        }

        if (recommendations.isEmpty()) {

            recommendations.add(
                    "Your financial activity appears stable."
            );
        }

        String summaryMessage =
                """
                AI financial agent analyzed
                your transaction activity,
                spending behavior,
                and risk profile successfully.
                """;

        return new AgentRecommendationResponse(
                "ACTIVE",
                recommendations,
                summaryMessage
        );
    }
}