package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.FraudAlertResponse;
import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutonomousMonitoringRuntime {

    private final UserRepository
            userRepository;

    private final FraudMonitoringAgentService
            fraudMonitoringAgentService;

    @Scheduled(
            initialDelayString = "${finpay.monitoring.initial-delay-ms:90000}",
            fixedDelayString = "${finpay.monitoring.fixed-delay-ms:300000}"
    )
    public void monitorFinancialRisk() {

        log.info(
                "AUTONOMOUS AI MONITORING STARTED"
        );

        List<User> users =
                userRepository.findAll();

        for (User user : users) {

            try {

                FraudAlertResponse response =
                        fraudMonitoringAgentService
                                .monitorFraudRisk(
                                        user
                                );

                if (Boolean.TRUE.equals(
                        response.getFraudDetected()
                )) {

                    log.warn(
                            """
                            AI FRAUD ALERT:
                            User ID: {}
                            Severity: {}
                            Message: {}
                            """,

                            user.getId(),
                            response.getSeverity(),
                            response.getAlertMessage()
                    );
                }

            } catch (Exception ex) {

                log.error(
                        "Autonomous fraud monitoring failed for user id={}: {}",
                        user.getId(),
                        ex.getMessage(),
                        ex
                );
            }
        }

        log.info(
                "AUTONOMOUS AI MONITORING COMPLETED"
        );
    }
}