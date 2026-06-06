package com.bookstore.kata.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.kata.dto.AddCartItemRequest;
import com.bookstore.kata.dto.CartResponse;
import com.bookstore.kata.dto.UpdateCartItemQuantityRequest;
import com.bookstore.kata.services.CartService;
import com.bookstore.kata.web.WebAuth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(HttpSession session) {
        return cartService.getCart(WebAuth.requireLoggedInUsername(session));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItem(HttpSession session, @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(WebAuth.requireLoggedInUsername(session), request);
    }

    @PatchMapping("/items/{bookId}")
    public CartResponse updateItemQuantity(
            HttpSession session,
            @PathVariable Long bookId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        return cartService.updateItemQuantity(WebAuth.requireLoggedInUsername(session), bookId, request);
    }

    @DeleteMapping("/items/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(HttpSession session, @PathVariable Long bookId) {
        cartService.removeItem(WebAuth.requireLoggedInUsername(session), bookId);
    }
}
