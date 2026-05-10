package com.finpay.backend.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycVerificationRequest {

    @NotBlank(message = "PAN number is required")

    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN format"
    )
    private String panNumber;

    @NotBlank(message = "Masked Aadhaar is required")

    @Pattern(
            regexp = "^XXXX-XXXX-[0-9]{4}$",
            message = "Invalid masked Aadhaar format"
    )
    private String aadhaarMasked;
}