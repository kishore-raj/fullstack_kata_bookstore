package com.bookstore.kata.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.kata.entities.User;

public interface UserRepository extends  JpaRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
