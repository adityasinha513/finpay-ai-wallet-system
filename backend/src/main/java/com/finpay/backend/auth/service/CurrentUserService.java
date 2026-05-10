package com.finpay.backend.auth.service;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.auth.repository.UserRepository;
import com.finpay.backend.common.exception.ResourceNotFoundException;
import com.finpay.backend.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Resolves the authenticated {@link User} once per request for controllers.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User requireCurrentUser() {

        String email = SecurityUtils.getCurrentUserEmail();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }
}
