package com.bookstore.kata.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.kata.dto.OrderResponse;
import com.bookstore.kata.services.OrderService;
import com.bookstore.kata.web.WebAuth;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(HttpSession session) {
        return orderService.placeOrder(WebAuth.requireLoggedInUsername(session));
    }

    @GetMapping("/list")
    public List<OrderResponse> listMyOrders(HttpSession session) {
        return orderService.listOrders(WebAuth.requireLoggedInUsername(session));
    }
}
