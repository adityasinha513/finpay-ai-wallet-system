package com.finpay.backend.analytics.controller;

import com.finpay.backend.analytics.dto.AIChatRequest;
import com.finpay.backend.analytics.dto.AIChatResponse;
import com.finpay.backend.analytics.dto.AgentRecommendationResponse;
import com.finpay.backend.analytics.dto.FinancialInsightResponse;
import com.finpay.backend.analytics.dto.FinancialSummaryResponse;
import com.finpay.backend.analytics.dto.RiskAnalysisResponse;
import com.finpay.backend.analytics.dto.TransactionBehaviorResponse;
import com.finpay.backend.analytics.service.AnalyticsService;
import com.finpay.backend.analytics.service.FinancialAgentService;
import com.finpay.backend.analytics.service.LLMFinancialAgentService;
import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.service.CurrentUserService;
import com.finpay.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.finpay.backend.analytics.dto.FraudAlertResponse;
import com.finpay.backend.analytics.service.FraudMonitoringAgentService;
import com.finpay.backend.analytics.dto.AgentSystemResponse;
import com.finpay.backend.analytics.service.MultiAgentCoordinatorService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService
            analyticsService;

    private final CurrentUserService
            currentUserService;

    private final FinancialAgentService
            financialAgentService;

    private final LLMFinancialAgentService
            llmFinancialAgentService;

    private final FraudMonitoringAgentService
        fraudMonitoringAgentService;

    private final MultiAgentCoordinatorService
        multiAgentCoordinatorService;
    @GetMapping("/summary")
    public ApiResponse<FinancialSummaryResponse>
    getFinancialSummary() {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        FinancialSummaryResponse response =
                analyticsService
                        .getFinancialSummary(
                                currentUser
                        );

        return ApiResponse.ok(
                "Financial summary fetched successfully",
                response
        );
    }

    @GetMapping("/insights")
    public ApiResponse<List<FinancialInsightResponse>>
    getFinancialInsights() {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        List<FinancialInsightResponse>
                insights =
                analyticsService
                        .getFinancialInsights(
                                currentUser
                        );

        return ApiResponse.ok(
                "Financial insights fetched successfully",
                insights
        );
    }

    @GetMapping("/behavior")
    public ApiResponse<TransactionBehaviorResponse>
    analyzeTransactionBehavior() {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        TransactionBehaviorResponse response =
                analyticsService
                        .analyzeTransactionBehavior(
                                currentUser
                        );

        return ApiResponse.ok(
                "Transaction behavior analyzed successfully",
                response
        );
    }

    @GetMapping("/risk")
    public ApiResponse<RiskAnalysisResponse>
    analyzeFinancialRisk() {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        RiskAnalysisResponse response =
                analyticsService
                        .analyzeFinancialRisk(
                                currentUser
                        );

        return ApiResponse.ok(
                "Financial risk analysis completed",
                response
        );
    }

    @GetMapping("/agent/recommendations")
    public ApiResponse<AgentRecommendationResponse>
    generateAgentRecommendations() {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        AgentRecommendationResponse response =
                financialAgentService
                        .generateFinancialRecommendations(
                                currentUser
                        );

        return ApiResponse.ok(
                "AI financial recommendations generated successfully",
                response
        );
    }

    @PostMapping("/agent/chat")
    public ApiResponse<AIChatResponse>
    chatWithFinancialAgent(
            @Valid @RequestBody
            AIChatRequest request
    ) {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        AIChatResponse response =
                llmFinancialAgentService
                        .chat(
                                currentUser,
                                request
                        );

        return ApiResponse.ok(
                "AI response generated successfully",
                response
        );
    }
    @GetMapping("/agent/fraud-monitor")
public ApiResponse<FraudAlertResponse>
monitorFraudRisk() {

    User currentUser =
            currentUserService
                    .requireCurrentUser();

    FraudAlertResponse response =
            fraudMonitoringAgentService
                    .monitorFraudRisk(
                            currentUser
                    );

    return ApiResponse.ok(
            "Fraud monitoring completed successfully",
            response
    );
}
    @GetMapping("/agent/system")
public ApiResponse<AgentSystemResponse>
executeAgentSystem() {

    User currentUser =
            currentUserService
                    .requireCurrentUser();

    AgentSystemResponse response =
            multiAgentCoordinatorService
                    .executeAgentWorkflow(
                            currentUser
                    );

    return ApiResponse.ok(
            "Multi-agent workflow executed successfully",
            response
    );
}
}