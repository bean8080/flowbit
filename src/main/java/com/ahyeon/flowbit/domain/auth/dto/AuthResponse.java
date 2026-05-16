package com.ahyeon.flowbit.domain.auth.dto;

import com.ahyeon.flowbit.domain.user.User;
import lombok.Getter;

@Getter
public class AuthResponse {

    private Long userId;

    private String email;

    private String name;

    public AuthResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}