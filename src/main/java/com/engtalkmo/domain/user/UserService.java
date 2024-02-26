package com.engtalkmo.domain.user;

import com.engtalkmo.domain.user.dto.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Long signUp(CreateUserRequest request) {
        return userRepository.save(User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .build()).getId();
    }
}
