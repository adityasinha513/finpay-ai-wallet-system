package com.finpay.backend.transaction.controller;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.service.CurrentUserService;
import com.finpay.backend.common.dto.ApiResponse;
import com.finpay.backend.transaction.dto.TransactionHistoryResponse;
import com.finpay.backend.transaction.dto.TransferRequest;
import com.finpay.backend.transaction.dto.TransferResponse;
import com.finpay.backend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService
            transactionService;

    private final CurrentUserService
            currentUserService;

    @PostMapping("/transfer")
    public ApiResponse<TransferResponse>
    transferMoney(
            @Valid @RequestBody
            TransferRequest request
    ) {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        TransferResponse response =
                transactionService
                        .transferMoney(
                                currentUser,
                                request
                        );

        return ApiResponse.ok(
                "Money transferred successfully",
                response
        );
    }

    @GetMapping
    public ApiResponse<Page<TransactionHistoryResponse>>
    getTransactionHistory(

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size
    ) {

        User currentUser =
                currentUserService
                        .requireCurrentUser();

        Page<TransactionHistoryResponse>
                transactions =
                transactionService
                        .getTransactionHistory(
                                currentUser,
                                page,
                                size
                        );

        return ApiResponse.ok(
                "Transaction history fetched successfully",
                transactions
        );
    }

    @GetMapping("/{referenceId}")
    public ApiResponse<TransactionHistoryResponse>
    getTransactionByReference(
            @PathVariable String referenceId
    ) {

        TransactionHistoryResponse transaction =
                transactionService
                        .getTransactionByReference(
                                referenceId
                        );

        return ApiResponse.ok(
                "Transaction fetched successfully",
                transaction
        );
    }

    @GetMapping("/type/{type}")
    public ApiResponse<Page<TransactionHistoryResponse>>
    getTransactionsByType(

            @PathVariable
            TransactionType type,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

    User currentUser =
            currentUserService
                    .requireCurrentUser();

    Page<TransactionHistoryResponse>
            transactions =
            transactionService
                    .getTransactionsByType(
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
public ApiResponse<Page<TransactionHistoryResponse>>
getTransactionsByStatus(

        @PathVariable
        TransactionStatus status,

        @RequestParam(defaultValue = "0")
        int page,

        @RequestParam(defaultValue = "10")
        int size
) {

    User currentUser =
            currentUserService
                    .requireCurrentUser();

    Page<TransactionHistoryResponse>
            transactions =
            transactionService
                    .getTransactionsByStatus(
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
}