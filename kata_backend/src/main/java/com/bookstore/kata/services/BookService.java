package com.bookstore.kata.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bookstore.kata.dto.BookResponse;
import com.bookstore.kata.entities.Book;
import com.bookstore.kata.repositories.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;


    public BookService(BookRepository bookRepository) {
    
        this.bookRepository = bookRepository;
    
    }

    public List<BookResponse> getAllBooks() {
                return bookRepository.findAll().stream().map(this::toResponse).toList();

    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(),book.getDescription());
    }    
}
