package com.bookstore.kata.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.kata.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_UsernameOrderByPlacedAtDesc(String username);
}
