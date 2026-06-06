package com.bookstore.kata.dto;

public record CartItemResponse(
        Long bookId,
        String title,
        String author,
        Double unitPrice,
        int quantity,
        double itemTotal
) {
}
