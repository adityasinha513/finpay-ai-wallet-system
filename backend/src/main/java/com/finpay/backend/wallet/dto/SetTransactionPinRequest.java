package com.finpay.backend.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetTransactionPinRequest {

    @NotBlank(message = "Transaction PIN is required")

    @Pattern(
            regexp = "\\d{4}",
            message = "PIN must be exactly 4 digits"
    )
    private String transactionPin;
}