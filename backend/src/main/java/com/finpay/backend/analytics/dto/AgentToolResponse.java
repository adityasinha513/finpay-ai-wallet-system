package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgentToolResponse {

    private String toolName;

    private Object result;
}