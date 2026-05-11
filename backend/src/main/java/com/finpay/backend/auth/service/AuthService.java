package com.finpay.backend.auth.service;

import com.finpay.backend.auth.dto.LoginRequest;
import com.finpay.backend.auth.dto.LoginResponse;
import com.finpay.backend.auth.dto.RegisterRequest;
import com.finpay.backend.auth.dto.RegisterResponse;
import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.enums.KycStatus;
import com.finpay.backend.auth.repository.UserRepository;
import com.finpay.backend.common.exception.ResourceAlreadyExistsException;
import com.finpay.backend.common.security.JwtUtil;
import com.finpay.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final WalletService walletService;

    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        User savedUser = userRepository.save(user);

        walletService.createWallet(savedUser);

        log.info(
                "New user registered: {}",
                savedUser.getEmail()
        );

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(
                request.getEmail()
        );

        log.info(
                "User logged in successfully: {}",
                request.getEmail()
        );

        return new LoginResponse(
                token,
                "Login successful"
        );
    }

    @CacheEvict(
            cacheNames = {
                    "wallets",
                    "walletBalance"
            },
            key = "#user.id"
    )
    public void setTransactionPin(
            User user,
            String transactionPin
    ) {

        user.setTransactionPin(
                passwordEncoder.encode(transactionPin)
        );

        userRepository.save(user);

        log.info(
                "Transaction PIN set for user: {}",
                user.getEmail()
        );
    }

    @CacheEvict(
            cacheNames = {
                    "wallets",
                    "walletBalance"
            },
            key = "#user.id"
    )
    public void completeKyc(
            User user,
            String panNumber,
            String aadhaarMasked
    ) {

        user.setPanNumber(panNumber);

        user.setAadhaarMasked(aadhaarMasked);

        user.setKycStatus(KycStatus.VERIFIED);

        userRepository.save(user);

        log.info(
                "KYC completed for user: {}",
                user.getEmail()
        );
    }
}
