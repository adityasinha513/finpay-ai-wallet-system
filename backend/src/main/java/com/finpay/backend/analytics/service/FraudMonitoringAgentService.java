package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.FraudAlertResponse;
import com.finpay.backend.analytics.dto.RiskAnalysisResponse;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudMonitoringAgentService {

    private final AnalyticsService
            analyticsService;

    public FraudAlertResponse
    monitorFraudRisk(
            User user
    ) {

        RiskAnalysisResponse risk =
                analyticsService
                        .analyzeFinancialRisk(
                                user
                        );

        boolean fraudDetected = false;

        String severity = "LOW";

        String alertMessage =
                "No suspicious activity detected.";

        if ("HIGH".equals(
                risk.getRiskLevel()
        )) {

            fraudDetected = true;

            severity = "HIGH";

            alertMessage =
                    """
                    Suspicious financial activity
                    detected. Immediate review
                    is recommended.
                    """;

        } else if ("MEDIUM".equals(
                risk.getRiskLevel()
        )) {

            fraudDetected = true;

            severity = "MEDIUM";

            alertMessage =
                    """
                    Moderate risk activity
                    detected in account behavior.
                    """;
        }

        return new FraudAlertResponse(
                fraudDetected,
                severity,
                alertMessage
        );
    }
}