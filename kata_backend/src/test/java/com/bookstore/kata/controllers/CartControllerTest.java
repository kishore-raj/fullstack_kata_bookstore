package com.bookstore.kata.controllers;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class CartControllerTest {

    private static final double CLEAN_CODE_PRICE = 42.99;

    @Autowired
    private MockMvc mockMvc;

    private static String uniqueUser() {
        return "cart_" + UUID.randomUUID().toString().substring(0, 8);
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
    void getCart_withoutSession_returns401() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Please log in first."));
    }

    @Test
    void getCart_empty_returns200() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalAmount").value(0.0))
                .andExpect(jsonPath("$.cartId").value(nullValue()));
    }

    @Test
    void addItem_returns201() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].bookId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.cartId").exists())
                .andExpect(jsonPath("$.cartId").isNumber())
                .andExpect(jsonPath("$.totalAmount").value(closeTo(2 * CLEAN_CODE_PRICE, 0.01)));
    }

    @Test
    void addItem_sameBook_mergesQuantity() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(3))
                .andExpect(jsonPath("$.items[0].itemTotal").value(closeTo(3 * CLEAN_CODE_PRICE, 0.01)));
    }

    @Test
    void patchQuantity_returns200() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/cart/items/1")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(1))
                .andExpect(jsonPath("$.totalAmount").value(closeTo(CLEAN_CODE_PRICE, 0.01)));
    }

    @Test
    void deleteItem_returns204() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/cart/items/1").session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void addItem_unknownBook_returns404() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":99999,\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void patchItem_notInCart_returns404() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":2,\"quantity\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/cart/items/1")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book not in cart"));
    }

    @Test
    void addItem_invalidQuantity_returns400() throws Exception {
        MockHttpSession session = registerAndLogin();
        mockMvc.perform(post("/cart/items")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists());
    }
}
