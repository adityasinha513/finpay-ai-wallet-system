package com.finpay.backend.transaction.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotBlank(
            message = "Receiver wallet number is required"
    )
    private String receiverWalletNumber;

    @NotNull(
            message = "Amount is required"
    )

    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than 0"
    )

    @DecimalMax(
            value = "999999999.99",
            message = "Amount exceeds allowed limit"
    )

    @Digits(
            integer = 9,
            fraction = 2,
            message = "Amount format is invalid"
    )
    private BigDecimal amount;

    @NotBlank(
            message = "Transaction PIN is required"
    )
    private String transactionPin;

    private String description;
}