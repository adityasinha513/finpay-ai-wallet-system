package com.finpay.backend.transaction.repository;

import com.finpay.backend.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.finpay.backend.transaction.enums.TransactionStatus;
import com.finpay.backend.transaction.enums.TransactionType;

import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

import java.math.BigDecimal;
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
@Query("""
       SELECT COALESCE(SUM(t.amount), 0)
       FROM Transaction t
       WHERE t.senderWallet.id = :walletId
       AND t.status = 'SUCCESS'
       """)
BigDecimal getTotalTransferred(
        Long walletId
);

@Query("""
       SELECT COALESCE(SUM(t.amount), 0)
       FROM Transaction t
       WHERE t.receiverWallet.id = :walletId
       AND t.status = 'SUCCESS'
       """)
BigDecimal getTotalReceived(
        Long walletId
);

@Query("""
       SELECT COUNT(t)
       FROM Transaction t
       WHERE (
            t.senderWallet.id = :walletId
            OR
            t.receiverWallet.id = :walletId
       )
       AND t.status = 'SUCCESS'
       """)
Long getTotalTransactionCount(
        Long walletId
);

@Query("""
       SELECT COALESCE(MAX(t.amount), 0)
       FROM Transaction t
       WHERE (
            t.senderWallet.id = :walletId
            OR
            t.receiverWallet.id = :walletId
       )
       AND t.status = 'SUCCESS'
       """)
BigDecimal getHighestTransaction(
        Long walletId
);

@Query("""
       SELECT COALESCE(AVG(t.amount), 0)
       FROM Transaction t
       WHERE (
            t.senderWallet.id = :walletId
            OR
            t.receiverWallet.id = :walletId
       )
       AND t.status = 'SUCCESS'
       """)
Double getAverageTransactionAmount(
        Long walletId
);

Long countBySenderWallet_IdAndAmountGreaterThan(
        Long walletId,
        java.math.BigDecimal amount
);
Long countBySenderWallet_IdAndCreatedAtAfter(
        Long walletId,
        LocalDateTime createdAt
);
}