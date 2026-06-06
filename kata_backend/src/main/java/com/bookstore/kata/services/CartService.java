package com.bookstore.kata.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bookstore.kata.dto.AddCartItemRequest;
import com.bookstore.kata.dto.CartItemResponse;
import com.bookstore.kata.dto.CartResponse;
import com.bookstore.kata.dto.UpdateCartItemQuantityRequest;
import com.bookstore.kata.entities.Book;
import com.bookstore.kata.entities.Cart;
import com.bookstore.kata.entities.CartItem;
import com.bookstore.kata.entities.User;
import com.bookstore.kata.repositories.BookRepository;
import com.bookstore.kata.repositories.CartRepository;
import com.bookstore.kata.repositories.UserRepository;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    public CartService(UserRepository userRepository, BookRepository bookRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        loadUser(username);
        return cartRepository.findByUser_Username(username)
                .map(this::toResponse)
                .orElse(new CartResponse(null, List.of(), 0.0));
    }

    @Transactional
    public CartResponse addItem(String username, AddCartItemRequest request) {
        User user = loadUser(username);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Cart cart = getOrCreateCart(user);

        for (CartItem item : cart.getItems()) {
            if (item.getBook().getId().equals(book.getId())) {
                item.setQuantity(item.getQuantity() + request.quantity());
                return toResponse(cartRepository.save(cart));
            }
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setBook(book);
        newItem.setQuantity(request.quantity());
        cart.getItems().add(newItem);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItemQuantity(String username, Long bookId, UpdateCartItemQuantityRequest request) {
        Cart cart = cartRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem item = findItem(cart, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not in cart"));
        item.setQuantity(request.quantity());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void removeItem(String username, Long bookId) {
        Cart cart = cartRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem item = findItem(cart, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not in cart"));
        cart.getItems().remove(item);
        cartRepository.save(cart);
    }

    private User loadUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or Unauthorized access, please login"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Username(user.getUsername())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    user.setCart(cart);
                    return cartRepository.save(cart);
                });
    }

    private Optional<CartItem> findItem(Cart cart, Long bookId) {
        return cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(bookId))
                .findFirst();
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        double total = 0.0;
        for (CartItem item : cart.getItems()) {
            Book b = item.getBook();
            double unit = b.getPrice() != null ? b.getPrice() : 0.0;
            double itemTotal = unit * item.getQuantity();
            total += itemTotal;
            items.add(new CartItemResponse(
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getPrice(),
                    item.getQuantity(),
                    itemTotal));
        }
        return new CartResponse(cart.getId(), items, total);
    }
}
