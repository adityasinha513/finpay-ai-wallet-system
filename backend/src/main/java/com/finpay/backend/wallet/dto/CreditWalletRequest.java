package com.finpay.backend.wallet.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreditWalletRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            inclusive = true,
            message = "Amount must be at least 0.01"
    )
    @DecimalMax(
            value = "999999999.99",
            message = "Amount exceeds maximum allowed per credit"
    )
    @Digits(
            integer = 9,
            fraction = 2,
            message = "Amount may have at most 9 integer digits and 2 decimal places"
    )
    private BigDecimal amount;
}
