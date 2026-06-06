package com.bookstore.kata.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bookstore.kata.dto.CartItemResponse;
import com.bookstore.kata.dto.OrderResponse;
import com.bookstore.kata.entities.Book;
import com.bookstore.kata.entities.Cart;
import com.bookstore.kata.entities.CartItem;
import com.bookstore.kata.entities.Order;
import com.bookstore.kata.entities.OrderItem;
import com.bookstore.kata.entities.User;
import com.bookstore.kata.repositories.CartRepository;
import com.bookstore.kata.repositories.OrderRepository;
import com.bookstore.kata.repositories.UserRepository;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    private static final String UNKNOWN_USER_MESSAGE =
        "User not found or Unauthorized access, please login";

    public OrderService(
            UserRepository userRepository,
            CartRepository cartRepository,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Creates an order from the current cart (snapshot prices), persists it, and clears the cart.
     */
    @Transactional
    public OrderResponse placeOrder(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNKNOWN_USER_MESSAGE));
        Cart cart = cartRepository.findByUser_Username(username).orElse(null);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setPlacedAt(LocalDateTime.now());

        double total = 0.0;
        for (CartItem cartItem : new ArrayList<>(cart.getItems())) {
            Book book = cartItem.getBook();
            double unit = book.getPrice() != null ? book.getPrice() : 0.0;
            int qty = cartItem.getQuantity();
            double itemTotal = unit * qty;
            total += itemTotal;

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(qty);
            item.setUnitPrice(unit);
            order.getItems().add(item);
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(String username) {
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNKNOWN_USER_MESSAGE));
        return orderRepository.findByUser_UsernameOrderByPlacedAtDesc(username).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<CartItemResponse> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            Book b = item.getBook();
            double unit = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
            int qty = item.getQuantity();
            double itemTotal = unit * qty;
            items.add(new CartItemResponse(
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    unit,
                    qty,
                    itemTotal));
        }
        double total = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
        return new OrderResponse(
                order.getId(),
                order.getPlacedAt().toString(),
                items,
                total);
    }
}
