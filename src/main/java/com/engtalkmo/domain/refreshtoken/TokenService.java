package com.engtalkmo.domain.refreshtoken;

import com.engtalkmo.config.jwt.TokenProvider;
import com.engtalkmo.domain.user.User;
import com.engtalkmo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String createAccessToken(String refreshToken) {

        // 토큰 유효성 검증을 한다. (실패하면 예외 처리)
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"))
                .getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));

        // 유저 정보와 함께 유효시간이 2시간으로 설정된 AccessToken 을 생성한다.
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
