package com.bookstore.kata.dto;

import java.util.List;

public record OrderResponse(Long orderId, String placedAt, List<CartItemResponse> items, double totalAmount) {
}
