package com.finpay.backend.wallet.controller;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.repository.UserRepository;
import com.finpay.backend.auth.service.AuthService;
import com.finpay.backend.common.exception.ResourceNotFoundException;
import com.finpay.backend.common.util.SecurityUtils;
import com.finpay.backend.wallet.dto.BalanceResponse;
import com.finpay.backend.wallet.dto.CreditWalletRequest;
import com.finpay.backend.wallet.dto.SetTransactionPinRequest;
import com.finpay.backend.wallet.dto.WalletResponse;
import com.finpay.backend.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.finpay.backend.wallet.dto.KycVerificationRequest;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping
    public WalletResponse getWallet() {

        String email =
                SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return walletService.getWalletByUserId(
                user.getId()
        );
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance() {

        String email =
                SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return walletService.getWalletBalance(
                user.getId()
        );
    }

    @PostMapping("/credit")
    public WalletResponse creditWallet(
            @Valid @RequestBody
            CreditWalletRequest request
    ) {

        String email =
                SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return walletService.creditWallet(
                user.getId(),
                request.getAmount()
        );
    }

    @PutMapping("/set-pin")
    public String setTransactionPin(
            @Valid @RequestBody
            SetTransactionPinRequest request
    ) {

        String email =
                SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        authService.setTransactionPin(
                user,
                request.getTransactionPin()
        );

        return "Transaction PIN set successfully";
    }
        @PutMapping("/complete-kyc")
    public String completeKyc(
            @Valid @RequestBody
            KycVerificationRequest request
    ) {

        String email =
                SecurityUtils.getCurrentUserEmail();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        authService.completeKyc(
                user,
                request.getPanNumber(),
                request.getAadhaarMasked()
        );

        return "KYC completed successfully";
    }
}