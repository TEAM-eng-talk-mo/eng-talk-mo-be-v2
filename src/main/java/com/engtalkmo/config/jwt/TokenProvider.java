package com.engtalkmo.config.jwt;

import com.engtalkmo.domain.user.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    // JWT Access Token & Refresh Token 을 생성한다.
    private String makeToken(Date expiry, User user) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // header typ: JWT
                .setIssuer(jwtProperties.getIssuer()) // payload iss: 토큰 발급자
                .setIssuedAt(now) // payload iat: 현재시간
                .setExpiration(expiry) // payload exp: 매개변수 expiry
                .setSubject(user.getEmail()) // payload sub: 토큰 제목 (유저의 이메일)
                .claim("id", user.getId()) // payload claim: 유저 id
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // signature 해시값 HS256 + 비밀키 암호화
                .compact();
    }

    // JWT 유효성을 검증한다.
    public boolean validToken(String token) {
        try {
            Jws<Claims> claim = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀키로 복호화
                    .parseClaimsJws(token);
            return claim.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) { // 복호화 과정에서 예외가 발생하면 검증 실패
            return false;
        }
    }

    // 토큰 기반으로 인증 정보를 가져온다.
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), "", authorities), token, authorities);
    }

    // 토큰 기반으로 유저 정보(id)를 가져온다.
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    // 토큰 기반으로 클레임(claim)을 가져온다.
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
