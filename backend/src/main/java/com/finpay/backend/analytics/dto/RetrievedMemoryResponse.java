package com.finpay.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetrievedMemoryResponse {

    private String userPrompt;

    private String aiResponse;
}