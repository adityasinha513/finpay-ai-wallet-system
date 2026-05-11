package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.FinancialSummaryResponse;
import com.finpay.backend.auth.entity.User;
import com.finpay.backend.common.exception.ResourceNotFoundException;
import com.finpay.backend.transaction.repository.TransactionRepository;
import com.finpay.backend.wallet.entity.Wallet;
import com.finpay.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import com.finpay.backend.analytics.dto.FinancialInsightResponse;
import com.finpay.backend.analytics.dto.TransactionBehaviorResponse;
import com.finpay.backend.analytics.dto.RiskAnalysisResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final WalletRepository walletRepository;

    private final TransactionRepository
            transactionRepository;

    public FinancialSummaryResponse
    getFinancialSummary(
            User user
    ) {

        Wallet wallet = walletRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        BigDecimal totalTransferred =
                transactionRepository
                        .getTotalTransferred(
                                wallet.getId()
                        );

        BigDecimal totalReceived =
                transactionRepository
                        .getTotalReceived(
                                wallet.getId()
                        );

        Long totalTransactions =
                transactionRepository
                        .getTotalTransactionCount(
                                wallet.getId()
                        );

        BigDecimal highestTransaction =
                transactionRepository
                        .getHighestTransaction(
                                wallet.getId()
                        );

        Double averageTransaction =
                transactionRepository
                        .getAverageTransactionAmount(
                                wallet.getId()
                        );

        return new FinancialSummaryResponse(
                totalTransferred,
                totalReceived,
                totalTransactions,
                highestTransaction,
                BigDecimal.valueOf(
                        averageTransaction
                )
        );
    }
    public List<FinancialInsightResponse>
getFinancialInsights(
        User user
) {

    Wallet wallet = walletRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Wallet not found"
                    )
            );

    List<FinancialInsightResponse>
            insights = new ArrayList<>();

    Long highValueTransfers =
            transactionRepository
                    .countBySenderWallet_IdAndAmountGreaterThan(
                            wallet.getId(),
                            BigDecimal.valueOf(10000)
                    );

    if (highValueTransfers > 0) {

        insights.add(
                new FinancialInsightResponse(
                        "HIGH_VALUE_ACTIVITY",

                        "High-value transaction activity detected in your account.",

                        "HIGH"
                )
        );
    }

    if (wallet.getBalance()
            .compareTo(BigDecimal.valueOf(1000)) < 0) {

        insights.add(
                new FinancialInsightResponse(
                        "LOW_BALANCE",

                        "Your wallet balance is running low.",

                        "MEDIUM"
                )
        );
    }

    if (insights.isEmpty()) {

        insights.add(
                new FinancialInsightResponse(
                        "NORMAL_ACTIVITY",

                        "Your account activity appears normal.",

                        "LOW"
                )
        );
    }

    return insights;
}
public TransactionBehaviorResponse
analyzeTransactionBehavior(
        User user
) {

    Wallet wallet = walletRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Wallet not found"
                    )
            );

    LocalDateTime sevenDaysAgo =
            LocalDateTime.now().minusDays(7);

    Long recentTransactionCount =
            transactionRepository
                    .countBySenderWallet_IdAndCreatedAtAfter(
                            wallet.getId(),
                            sevenDaysAgo
                    );

    String activityLevel;
    String behaviorMessage;

    if (recentTransactionCount >= 20) {

        activityLevel = "HIGH";

        behaviorMessage =
                "Your transaction activity is unusually high this week.";

    } else if (recentTransactionCount >= 10) {

        activityLevel = "MEDIUM";

        behaviorMessage =
                "You have moderate transaction activity.";

    } else {

        activityLevel = "LOW";

        behaviorMessage =
                "Your transaction activity is currently low.";
    }

    return new TransactionBehaviorResponse(
            recentTransactionCount,
            activityLevel,
            behaviorMessage
    );
}
public RiskAnalysisResponse
analyzeFinancialRisk(
        User user
) {

    Wallet wallet = walletRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Wallet not found"
                    )
            );

    int riskScore = 0;

    Long highValueTransfers =
            transactionRepository
                    .countBySenderWallet_IdAndAmountGreaterThan(
                            wallet.getId(),
                            BigDecimal.valueOf(10000)
                    );

    if (highValueTransfers > 0) {

        riskScore += 40;
    }

    LocalDateTime sevenDaysAgo =
            LocalDateTime.now().minusDays(7);

    Long recentTransactionCount =
            transactionRepository
                    .countBySenderWallet_IdAndCreatedAtAfter(
                            wallet.getId(),
                            sevenDaysAgo
                    );

    if (recentTransactionCount >= 20) {

        riskScore += 30;
    }

    if (wallet.getBalance()
            .compareTo(BigDecimal.valueOf(1000)) < 0) {

        riskScore += 20;
    }

    String riskLevel;
    String analysis;

    if (riskScore >= 70) {

        riskLevel = "HIGH";

        analysis =
                "High financial risk activity detected.";

    } else if (riskScore >= 40) {

        riskLevel = "MEDIUM";

        analysis =
                "Moderate financial risk detected.";

    } else {

        riskLevel = "LOW";

        analysis =
                "Account activity appears stable.";
    }

    return new RiskAnalysisResponse(
            riskScore,
            riskLevel,
            analysis
    );
}
}