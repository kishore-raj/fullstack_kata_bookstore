package com.bookstore.kata.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class BooksControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void listBooks_shouldReturn200() throws Exception {

      MvcResult result =  mockMvc.perform(MockMvcRequestBuilders.get("/books/all"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();
        
        System.out.println("Testing books controller list response " + result.getResponse().getContentAsString());

    
        
        
    }



    
}
