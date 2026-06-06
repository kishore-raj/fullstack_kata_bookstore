package com.bookstore.kata.controllers;

import static org.hamcrest.Matchers.containsString;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static String uniqueUser() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void register_returns201() throws Exception {
        String u = uniqueUser();
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"password12\",\"email\":\"" + u + "@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(u))
                .andExpect(jsonPath("$.email").value(u + "@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        String u = uniqueUser();
        String body = "{\"username\":\"" + u + "\",\"password\":\"password12\",\"email\":\"" + u + "@example.com\"}";
        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/auth/register").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        String email = uniqueUser() + "@example.com";
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"a_" + uniqueUser()
                                + "\",\"password\":\"password12\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"b_" + uniqueUser()
                                + "\",\"password\":\"password12\",\"email\":\"" + email + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"ab\",\"password\":\"password12\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void register_shortPassword_returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"validname\",\"password\":\"short\",\"email\":\"a@b.co\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void login_thenLogout() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(u));

        mockMvc.perform(post("/auth/logout").session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void login_badPassword_returns401() throws Exception {
        String u = uniqueUser();
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"password12\",\"email\":\"" + u + "@ex.com\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + u + "\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_wrongMethod_returns405() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.message", containsString("GET")));
    }
}
