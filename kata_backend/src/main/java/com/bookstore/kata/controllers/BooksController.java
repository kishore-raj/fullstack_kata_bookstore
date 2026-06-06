package com.bookstore.kata.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.kata.dto.BookResponse;
import com.bookstore.kata.services.BookService;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/all")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }
}
