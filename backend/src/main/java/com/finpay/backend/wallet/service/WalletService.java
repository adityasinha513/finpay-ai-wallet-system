package com.finpay.backend.wallet.service;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.common.exception.ResourceNotFoundException;
import com.finpay.backend.common.exception.WalletOperationException;
import com.finpay.backend.wallet.dto.BalanceResponse;
import com.finpay.backend.wallet.dto.WalletResponse;
import com.finpay.backend.wallet.entity.Wallet;
import com.finpay.backend.wallet.enums.WalletStatus;
import com.finpay.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet createWallet(User user) {

        Wallet wallet = new Wallet();

        wallet.setUser(user);

        wallet.setWalletNumber(
                generateWalletNumber()
        );

        return walletRepository.save(wallet);
    }

    @Cacheable(
            value = "wallets",
            key = "#userId"
    )
    public WalletResponse getWalletByUserId(
            Long userId
    ) {

        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        return mapToWalletResponse(wallet);
    }

    @Cacheable(
            value = "walletBalance",
            key = "#userId"
    )
    public BalanceResponse getWalletBalance(
            Long userId
    ) {

        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        return new BalanceResponse(
                wallet.getBalance()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(
            cacheNames = {
                    "wallets",
                    "walletBalance"
            },
            key = "#userId"
    )
    public WalletResponse creditWallet(
            Long userId,
            BigDecimal amount
    ) {

        Wallet wallet = walletRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Wallet not found"
                        )
                );

        validateWalletStatus(wallet);

        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        Wallet updatedWallet =
                walletRepository.save(wallet);

        return mapToWalletResponse(
                updatedWallet
        );
    }

    private void validateWalletStatus(
            Wallet wallet
    ) {

        if (wallet.getStatus()
                != WalletStatus.ACTIVE) {

            throw new WalletOperationException(
                    "Wallet is not active"
            );
        }
    }

    private WalletResponse mapToWalletResponse(
            Wallet wallet
    ) {

        return new WalletResponse(
                wallet.getWalletNumber(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getStatus().name(),
                wallet.getDailyTransferLimit(),
                wallet.getDailyTransferredAmount(),
                wallet.getUser()
                        .getKycStatus()
                        .name()
        );
    }

    private String generateWalletNumber() {

        Random random = new Random();

        return "WALLET" +
                (10000000 + random.nextInt(90000000));
    }
}
