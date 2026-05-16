package com.ahyeon.flowbit.controller;

import com.ahyeon.flowbit.domain.auth.AuthService;
import com.ahyeon.flowbit.domain.auth.dto.AuthResponse;
import com.ahyeon.flowbit.domain.auth.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }
}