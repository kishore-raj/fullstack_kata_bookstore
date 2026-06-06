package com.bookstore.kata.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.kata.entities.Book;

public interface  BookRepository extends JpaRepository<Book, Long> {

    
    
}
