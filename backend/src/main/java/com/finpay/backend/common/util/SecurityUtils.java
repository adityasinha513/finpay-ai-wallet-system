package com.finpay.backend.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Not authenticated");
        }

        String name = authentication.getName();

        if (name == null || "anonymousUser".equals(name)) {
            throw new IllegalStateException("Not authenticated");
        }

        return name;
    }
}
