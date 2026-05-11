package com.finpay.backend.wallet.controller;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.service.AuthService;
import com.finpay.backend.auth.service.CurrentUserService;
import com.finpay.backend.common.dto.ApiResponse;
import com.finpay.backend.wallet.dto.BalanceResponse;
import com.finpay.backend.wallet.dto.CreditWalletRequest;
import com.finpay.backend.wallet.dto.KycVerificationRequest;
import com.finpay.backend.wallet.dto.SetTransactionPinRequest;
import com.finpay.backend.wallet.dto.WalletResponse;
import com.finpay.backend.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final AuthService authService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ApiResponse<WalletResponse> getWallet() {

        User user = currentUserService.requireCurrentUser();

        return ApiResponse.ok(
                "OK",
                walletService.getWalletByUserId(user.getId())
        );
    }

    @GetMapping("/balance")
    public ApiResponse<BalanceResponse> getBalance() {

        User user = currentUserService.requireCurrentUser();

        return ApiResponse.ok(
                "OK",
                walletService.getWalletBalance(user.getId())
        );
    }

    @PostMapping("/credit")
    public ApiResponse<WalletResponse> creditWallet(
            @Valid @RequestBody
            CreditWalletRequest request
    ) {

        User user = currentUserService.requireCurrentUser();

        return ApiResponse.ok(
                "Wallet credited",
                walletService.creditWallet(
                        user.getId(),
                        request.getAmount()
                )
        );
    }

    @PutMapping("/set-pin")
    public ApiResponse<Void> setTransactionPin(
            @Valid @RequestBody
            SetTransactionPinRequest request
    ) {

        User user = currentUserService.requireCurrentUser();

        authService.setTransactionPin(
                user,
                request.getTransactionPin()
        );

        return ApiResponse.ok(
                "Transaction PIN set successfully",
                null
        );
    }

    @PutMapping("/complete-kyc")
    public ApiResponse<Void> completeKyc(
            @Valid @RequestBody
            KycVerificationRequest request
    ) {

        User user = currentUserService.requireCurrentUser();

        authService.completeKyc(
                user,
                request.getPanNumber(),
                request.getAadhaarMasked()
        );

        return ApiResponse.ok(
                "KYC completed successfully",
                null
        );
    }
}
