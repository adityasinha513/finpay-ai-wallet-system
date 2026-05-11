package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AgentRecommendationResponse {

    private String agentStatus;

    private List<String> recommendations;

    private String summary;
}