package com.finpay.backend.transaction.controller;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.service.CurrentUserService;
import com.finpay.backend.common.dto.ApiResponse;
import com.finpay.backend.transaction.dto.TransactionHistoryResponse;
import com.finpay.backend.transaction.dto.TransferRequest;
import com.finpay.backend.transaction.dto.TransferResponse;
import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;
import com.finpay.backend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    private final CurrentUserService currentUserService;

    @PostMapping("/transfer")
    public ApiResponse<TransferResponse> transferMoney(
            @Valid @RequestBody TransferRequest request
    ) {

        User currentUser = currentUserService.requireCurrentUser();

        TransferResponse response =
                transactionService.transferMoney(
                        currentUser,
                        request
                );

        return ApiResponse.ok(
                "Money transferred successfully",
                response
        );
    }

    @GetMapping
    public ApiResponse<Page<TransactionHistoryResponse>> getTransactionHistory(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {

        User currentUser = currentUserService.requireCurrentUser();

        Page<TransactionHistoryResponse> transactions =
                transactionService.getTransactionHistory(
                        currentUser,
                        page,
                        size
                );

        return ApiResponse.ok(
                "Transaction history fetched successfully",
                transactions
        );
    }

    /**
     * Static path segments must be registered before {@code /{referenceId}} so
     * {@code /type/...} and {@code /status/...} are not captured as a reference id.
     */
    @GetMapping("/type/{type}")
    public ApiResponse<Page<TransactionHistoryResponse>> getTransactionsByType(
            @PathVariable TransactionType type,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {

        User currentUser = currentUserService.requireCurrentUser();

        Page<TransactionHistoryResponse> transactions =
                transactionService.getTransactionsByType(
                        currentUser,
                        type,
                        page,
                        size
                );

        return ApiResponse.ok(
                "Transactions fetched successfully",
                transactions
        );
    }

    @GetMapping("/status/{status}")
    public ApiResponse<Page<TransactionHistoryResponse>> getTransactionsByStatus(
            @PathVariable TransactionStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {

        User currentUser = currentUserService.requireCurrentUser();

        Page<TransactionHistoryResponse> transactions =
                transactionService.getTransactionsByStatus(
                        currentUser,
                        status,
                        page,
                        size
                );

        return ApiResponse.ok(
                "Transactions fetched successfully",
                transactions
        );
    }

    @GetMapping("/{referenceId}")
    public ApiResponse<TransactionHistoryResponse> getTransactionByReference(
            @PathVariable String referenceId
    ) {

        User currentUser = currentUserService.requireCurrentUser();

        TransactionHistoryResponse transaction =
                transactionService.getTransactionByReference(
                        currentUser,
                        referenceId
                );

        return ApiResponse.ok(
                "Transaction fetched successfully",
                transaction
        );
    }
}
