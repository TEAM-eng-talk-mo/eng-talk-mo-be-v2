package com.engtalkmo.domain.user.dto;

public record CreateUserRequest(
        String email,
        String password,
        String name) {
}
