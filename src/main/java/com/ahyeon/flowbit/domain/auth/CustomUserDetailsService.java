package com.ahyeon.flowbit.domain.auth;

import com.ahyeon.flowbit.domain.user.User;
import com.ahyeon.flowbit.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        User user=userRepository.findByEmail(email)
                .orElseThrow(
                        ()->new UsernameNotFoundException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        return new CustomUserDetails(user);
    }
}