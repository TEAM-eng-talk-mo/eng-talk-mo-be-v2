package com.engtalkmo.api;

import com.engtalkmo.domain.refreshtoken.TokenService;
import com.engtalkmo.domain.refreshtoken.dto.CreateAccessTokenRequest;
import com.engtalkmo.domain.refreshtoken.dto.CreateAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String accessToken = tokenService.createAccessToken(request.refreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(accessToken));
    }
}
