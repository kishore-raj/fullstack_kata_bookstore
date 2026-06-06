package com.bookstore.kata.controllers;

import static org.hamcrest.Matchers.closeTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private static final double CLEAN_CODE_PRICE = 42.99;

    @Autowired
    private MockMvc mockMvc;

    private static String uniqueUser() {
        return "ord_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private MockHttpSession registerAndLogin() throws Exception {
        String u = uniqueUser();
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"password12\",\"email\":\"" + u + "@ex.com\"}"))
                .andExpect(status().isCreated());
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/auth/login")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"password12\"}"))
                .andExpect(status().isOk());
        return session;
    }

    @Test
    void postOrders_withoutSession_returns401() throws Exception {
        mockMvc.perform(post("/orders/checkout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Please log in first."));
    }

    @Test
    void getOrders_withoutSession_returns401() throws Exception {
        mockMvc.perform(get("/orders/list"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postOrders_emptyCart_returns400() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/orders/checkout").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }

    @Test
    void postOrders_success_returns201AndClearsCart() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/orders/checkout").session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].bookId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalAmount").value(closeTo(2 * CLEAN_CODE_PRICE, 0.01)))
                .andExpect(jsonPath("$.placedAt").exists());

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void postOrders_twiceSecondCall_emptyCart_returns400() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/orders/checkout").session(session))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/orders/checkout").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }

    @Test
    void getOrders_returnsPlacedOrder() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":2,\"quantity\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/orders/checkout").session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists());

        mockMvc.perform(get("/orders/list").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").exists())
                .andExpect(jsonPath("$[0].items[0].bookId").value(2))
                .andExpect(jsonPath("$[0].items[0].title").value("Effective Java"));
    }
}
