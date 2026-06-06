package com.bookstore.kata.dto;

// In Java, a record provides several options and features out-of-the-box:
// 1. Compact declaration of an immutable data class.
// 2. Automatically provides final fields, a canonical constructor, getters (named as field()), equals(), hashCode(), and toString().
// 3. You can declare additional methods, static fields, static methods, and compact constructors inside the record.

// Example showing available options in a record:
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
