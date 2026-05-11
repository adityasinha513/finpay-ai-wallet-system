package com.finpay.backend.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AIChatRequest {

    @NotBlank
    private String prompt;
}