package com.example.product_management.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Management API")
                        .version("1.0.0")
                        .description("API documentation for product management system")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}
