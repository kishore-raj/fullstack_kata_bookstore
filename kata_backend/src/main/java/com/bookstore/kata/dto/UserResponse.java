package com.bookstore.kata.dto;

import java.time.LocalDateTime;

public record UserResponse(String username, String email, String status, LocalDateTime createdAt) {
}