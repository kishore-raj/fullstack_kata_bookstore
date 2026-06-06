package com.bookstore.kata.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(int status, String message, Map<String, String> fieldErrors) {

    public ApiError(int status, String message) {
        this(status, message, null);
    }
}
