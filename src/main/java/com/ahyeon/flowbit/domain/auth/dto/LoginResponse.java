package com.ahyeon.flowbit.domain.auth.dto;

import com.ahyeon.flowbit.domain.user.User;
import lombok.Getter;

@Getter
public class LoginResponse {

    private Long userId;

    private String email;

    private String name;

    private String accessToken;

    public LoginResponse(User user, String accessToken) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.accessToken = accessToken;
    }
}