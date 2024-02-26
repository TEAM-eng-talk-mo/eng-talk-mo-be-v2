package com.engtalkmo.domain.user.dto;

public record CreateUserRequest(
        String email,
        String username,
        String password) {
}
