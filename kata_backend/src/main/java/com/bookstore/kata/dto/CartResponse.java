package com.bookstore.kata.dto;

import java.util.List;

public record CartResponse(Long cartId, List<CartItemResponse> items, double totalAmount) {
}
