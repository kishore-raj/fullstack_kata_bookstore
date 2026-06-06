package com.bookstore.kata.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.bookstore.kata.security.SessionConstants;

import jakarta.servlet.http.HttpSession;

public final class WebAuth {

    private WebAuth() {
    }

    public static String requireLoggedInUsername(HttpSession session) {
        Object value = session.getAttribute(SessionConstants.LOGGED_IN_USERNAME);
        if (!(value instanceof String username) || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in first.");
        }
        return username;
    }
}
