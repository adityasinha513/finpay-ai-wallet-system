package com.finpay.backend.transaction.service;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.enums.KycStatus;
import com.finpay.backend.common.exception.InsufficientBalanceException;
import com.finpay.backend.common.exception.ResourceNotFoundException;
import com.finpay.backend.common.exception.WalletOperationException;
import com.finpay.backend.transaction.dto.TransactionHistoryResponse;
import com.finpay.backend.transaction.dto.TransferRequest;
import com.finpay.backend.transaction.dto.TransferResponse;
import com.finpay.backend.transaction.entity.Transaction;
import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;
import com.finpay.backend.transaction.repository.TransactionRepository;
import com.finpay.backend.wallet.entity.Wallet;
import com.finpay.backend.wallet.enums.WalletStatus;
import com.finpay.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    private final PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager;

    @Transactional
    public TransferResponse transferMoney(
            User sender,
            TransferRequest request
    ) {

        Wallet senderWallet = walletRepository
                .findByUserId(sender.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sender wallet not found"
                        )
                );

        Wallet receiverWallet = walletRepository
                .findByWalletNumber(
                        request.getReceiverWalletNumber()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Receiver wallet not found"
                        )
                );

        validateTransfer(
                sender,
                senderWallet,
                receiverWallet,
                request
        );

        senderWallet.setBalance(
                senderWallet.getBalance()
                        .subtract(request.getAmount())
        );

        senderWallet.setDailyTransferredAmount(
                senderWallet
                        .getDailyTransferredAmount()
                        .add(request.getAmount())
        );

        receiverWallet.setBalance(
                receiverWallet.getBalance()
                        .add(request.getAmount())
        );

        walletRepository.save(senderWallet);

        walletRepository.save(receiverWallet);

        String referenceId =
                generateReferenceId();

        Transaction transaction =
                new Transaction();

        transaction.setReferenceId(referenceId);

        transaction.setSenderWallet(
                senderWallet
        );

        transaction.setReceiverWallet(
                receiverWallet
        );

        transaction.setAmount(
                request.getAmount()
        );

        transaction.setType(
                TransactionType.TRANSFER
        );

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setDescription(
                request.getDescription()
        );

        transactionRepository.save(
                transaction
        );

        Long receiverUserId =
                receiverWallet.getUser().getId();

        evictWalletCaches(sender.getId());

        evictWalletCaches(receiverUserId);

        return new TransferResponse(
                referenceId,
                senderWallet.getWalletNumber(),
                receiverWallet.getWalletNumber(),
                request.getAmount(),
                transaction.getStatus().name(),
                "Transfer successful"
        );
    }

    public Page<TransactionHistoryResponse>
    getTransactionHistory(
            User user,
            int page,
            int size
    ) {

        Wallet wallet = walletRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        Page<Transaction> transactions =
                transactionRepository
                        .findBySenderWallet_IdOrReceiverWallet_Id(
                                wallet.getId(),
                                wallet.getId(),
                                PageRequest.of(page, size)
                        );

        return transactions.map(
                this::mapToHistoryResponse
        );
    }

    public Page<TransactionHistoryResponse>
getTransactionsByType(
        User user,
        TransactionType type,
        int page,
        int size
) {

    Wallet wallet = walletRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Wallet not found"
                    )
            );

    Page<Transaction> transactions =
            transactionRepository
                    .findBySenderWallet_IdOrReceiverWallet_IdAndType(
                            wallet.getId(),
                            wallet.getId(),
                            type,
                            PageRequest.of(page, size)
                    );

    return transactions.map(
            this::mapToHistoryResponse
    );
}
    public TransactionHistoryResponse
    getTransactionByReference(
            User user,
            String referenceId
    ) {

        Wallet userWallet = walletRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        Transaction transaction =
                transactionRepository
                        .findByReferenceId(referenceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Transaction not found"
                                )
                        );

        Long wid = userWallet.getId();

        if (!transaction.getSenderWallet().getId().equals(wid)
                && !transaction.getReceiverWallet().getId().equals(wid)) {

            throw new ResourceNotFoundException(
                    "Transaction not found"
            );
        }

        return mapToHistoryResponse(
                transaction
        );
    }

    public Page<TransactionHistoryResponse>
getTransactionsByStatus(
        User user,
        TransactionStatus status,
        int page,
        int size
) {

    Wallet wallet = walletRepository
            .findByUserId(user.getId())
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Wallet not found"
                    )
            );

    Page<Transaction> transactions =
            transactionRepository
                    .findBySenderWallet_IdOrReceiverWallet_IdAndStatus(
                            wallet.getId(),
                            wallet.getId(),
                            status,
                            PageRequest.of(page, size)
                    );

    return transactions.map(
            this::mapToHistoryResponse
    );
}
    private void validateTransfer(
            User sender,
            Wallet senderWallet,
            Wallet receiverWallet,
            TransferRequest request
    ) {

        if (senderWallet.getStatus()
                != WalletStatus.ACTIVE) {

            throw new WalletOperationException(
                    "Sender wallet is not active"
            );
        }

        if (receiverWallet.getStatus()
                != WalletStatus.ACTIVE) {

            throw new WalletOperationException(
                    "Receiver wallet is not active"
            );
        }

        if (sender.getKycStatus()
                != KycStatus.VERIFIED) {

            throw new WalletOperationException(
                    "KYC verification required for transfers"
            );
        }

        BigDecimal updatedDailyAmount =
                senderWallet
                        .getDailyTransferredAmount()
                        .add(request.getAmount());

        if (updatedDailyAmount.compareTo(
                senderWallet.getDailyTransferLimit()
        ) > 0) {

            throw new WalletOperationException(
                    "Daily transfer limit exceeded"
            );
        }

        if (!passwordEncoder.matches(
                request.getTransactionPin(),
                sender.getTransactionPin()
        )) {

            throw new WalletOperationException(
                    "Invalid transaction PIN"
            );
        }

        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient wallet balance"
            );
        }

        if (senderWallet.getWalletNumber()
                .equals(receiverWallet.getWalletNumber())) {

            throw new WalletOperationException(
                    "Cannot transfer to same wallet"
            );
        }
    }

    private TransactionHistoryResponse
    mapToHistoryResponse(
            Transaction transaction
    ) {

        return new TransactionHistoryResponse(
                transaction.getReferenceId(),

                transaction.getSenderWallet()
                        .getWalletNumber(),

                transaction.getReceiverWallet()
                        .getWalletNumber(),

                transaction.getAmount(),

                transaction.getType().name(),

                transaction.getStatus().name(),

                transaction.getDescription(),

                transaction.getCreatedAt()
        );
    }

    private String generateReferenceId() {

        return "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 12)
                        .toUpperCase();
    }

    private void evictWalletCaches(Long userId) {

        if (userId == null) {
            return;
        }

        Cache wallets = cacheManager.getCache("wallets");

        if (wallets != null) {
            wallets.evict(userId);
        }

        Cache walletBalance = cacheManager.getCache("walletBalance");

        if (walletBalance != null) {
            walletBalance.evict(userId);
        }
    }
}