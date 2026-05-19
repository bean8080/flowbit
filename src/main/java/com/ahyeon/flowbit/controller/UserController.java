package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.auth.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public Long me() {
        return currentUserProvider.getCurrentUserId();
    }
}