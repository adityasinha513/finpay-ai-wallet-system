package com.finpay.backend.transaction.repository;

import com.finpay.backend.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;
import java.util.Optional;
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
            Page<Transaction> findBySenderWallet_IdOrReceiverWallet_Id(
        Long senderWalletId,
        Long receiverWalletId,
        Pageable pageable
);

Optional<Transaction> findByReferenceId(
        String referenceId
);
Page<Transaction>
findBySenderWallet_IdOrReceiverWallet_IdAndType(
        Long senderWalletId,
        Long receiverWalletId,
        TransactionType type,
        Pageable pageable
);

Page<Transaction>
findBySenderWallet_IdOrReceiverWallet_IdAndStatus(
        Long senderWalletId,
        Long receiverWalletId,
        TransactionStatus status,
        Pageable pageable
);
}