package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FraudAlertResponse {

    private Boolean fraudDetected;

    private String severity;

    private String alertMessage;
}