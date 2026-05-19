package com.ahyeon.flowbit.domain.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "인증된 사용자가 없습니다."
            );
        }

        Object principal =
                authentication.getPrincipal();

        CustomUserDetails userDetails =
                (CustomUserDetails) principal;

        return userDetails
                .getUser()
                .getId();
    }
}