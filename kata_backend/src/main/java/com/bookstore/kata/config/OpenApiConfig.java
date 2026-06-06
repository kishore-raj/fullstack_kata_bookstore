package com.bookstore.kata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookstoreOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kata Bookstore API")
                        .version("1.0")
                        .description("""
                                Books, session-based auth (cookie JSESSIONID after login), shopping cart, and orders.

                                **Try protected routes:** use **Authorize** in Swagger UI is not enough for cookie sessions — call **POST /auth/login** first with *Try it out*, then reuse the session cookie for cart/orders (or use Postman / browser)."""));
    }
}
