package com.ahyeon.flowbit.domain.auth;

import com.ahyeon.flowbit.domain.auth.dto.AuthResponse;
import com.ahyeon.flowbit.domain.auth.dto.SignupRequest;
import com.ahyeon.flowbit.domain.user.User;
import com.ahyeon.flowbit.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);

        return new AuthResponse(savedUser);
    }
}