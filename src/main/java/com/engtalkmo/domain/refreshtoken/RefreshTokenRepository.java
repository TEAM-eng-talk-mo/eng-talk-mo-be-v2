package com.engtalkmo.domain.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);

    Optional<RefreshToken> findByRefreshToken(@Param("refreshToken") String refreshToken);
}
