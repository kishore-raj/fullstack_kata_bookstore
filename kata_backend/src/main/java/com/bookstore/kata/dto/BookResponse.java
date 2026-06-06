package com.bookstore.kata.dto;


public record BookResponse(
    Long id,
    String title,
    String author,
    Double price,
    String description
) {
    // small small validations under this constructor
    public BookResponse {
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
   
      
}
