package com.ahyeon.flowbit.domain.auth;

import com.ahyeon.flowbit.domain.auth.dto.AuthResponse;
import com.ahyeon.flowbit.domain.auth.dto.LoginRequest;
import com.ahyeon.flowbit.domain.auth.dto.LoginResponse;
import com.ahyeon.flowbit.domain.auth.dto.SignupRequest;
import com.ahyeon.flowbit.domain.user.User;
import com.ahyeon.flowbit.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getName(),
                LocalDateTime.now()
        );

        try {

            User savedUser =
                    userRepository.save(user);

            return new AuthResponse(savedUser);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "이미 사용 중인 이메일입니다."
            );
        }
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);

        return new LoginResponse(user, accessToken);
    }
}