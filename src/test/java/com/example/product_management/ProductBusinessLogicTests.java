package com.example.product_management;

import com.example.product_management.dto.ProductRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductBusinessLogicTests {
    
    // Builder de test pour ProductRequest
    static class TestProductRequestBuilder {
        private String name = "Default Product";
        private String description = "Default description with enough characters";
        private BigDecimal price = new BigDecimal("99.99");
        private Integer quantity = 10;
        private String category = "Default Category";
        
        TestProductRequestBuilder withName(String name) {
            this.name = name;
            return this;
        }
        
        TestProductRequestBuilder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        TestProductRequestBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        TestProductRequestBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
        
        TestProductRequestBuilder withCategory(String category) {
            this.category = category;
            return this;
        }
        
        ProductRequest build() {
            return ProductRequest.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .quantity(quantity)
                    .category(category)
                    .build();
        }
    }
    
    @Test
    void calculateTotalValue_SingleProduct() {
        // Given
        ProductRequest request = new TestProductRequestBuilder()
                .withPrice(new BigDecimal("100.00"))
                .withQuantity(5)
                .build();
        
        // When
        BigDecimal totalValue = request.getPrice()
                .multiply(new BigDecimal(request.getQuantity()));
        
        // Then
        assertThat(totalValue).isEqualTo(new BigDecimal("500.00"));
    }
    
    @Test
    void calculateTotalValue_MultipleProducts() {
        // Given
        List<ProductRequest> products = Arrays.asList(
                new TestProductRequestBuilder()
                        .withName("Product A")
                        .withPrice(new BigDecimal("50.00"))
                        .withQuantity(3)
                        .build(),
                new TestProductRequestBuilder()
                        .withName("Product B")
                        .withPrice(new BigDecimal("75.00"))
                        .withQuantity(2)
                        .build(),
                new TestProductRequestBuilder()
                        .withName("Product C")
                        .withPrice(new BigDecimal("100.00"))
                        .withQuantity(1)
                        .build()
        );
        
        // When
        BigDecimal totalValue = products.stream()
                .map(p -> p.getPrice().multiply(new BigDecimal(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Then
        assertThat(totalValue).isEqualTo(new BigDecimal("400.00"));
    }
    
    @Test
    void isLowStock_ThresholdCheck() {
        // Given
        ProductRequest lowStock = new TestProductRequestBuilder()
                .withQuantity(3)
                .build();
        
        ProductRequest normalStock = new TestProductRequestBuilder()
                .withQuantity(15)
                .build();
        
        int threshold = 5;
        
        // Then
        assertThat(lowStock.getQuantity() <= threshold).isTrue();
        assertThat(normalStock.getQuantity() <= threshold).isFalse();
    }
    
    @Test
    void productComparison_ByPrice() {
        // Given
        ProductRequest cheapProduct = new TestProductRequestBuilder()
                .withName("Cheap")
                .withPrice(new BigDecimal("49.99"))
                .build();
        
        ProductRequest expensiveProduct = new TestProductRequestBuilder()
                .withName("Expensive")
                .withPrice(new BigDecimal("199.99"))
                .build();
        
        // Then
        assertThat(cheapProduct.getPrice())
                .isLessThan(expensiveProduct.getPrice());
        
        assertThat(expensiveProduct.getPrice())
                .isGreaterThan(cheapProduct.getPrice());
    }
    
    @Test
    void productCategorization() {
        // Given
        List<ProductRequest> products = Arrays.asList(
                new TestProductRequestBuilder()
                        .withName("iPhone")
                        .withCategory("Electronics")
                        .build(),
                new TestProductRequestBuilder()
                        .withName("Java Book")
                        .withCategory("Books")
                        .build(),
                new TestProductRequestBuilder()
                        .withName("Laptop")
                        .withCategory("Electronics")
                        .build(),
                new TestProductRequestBuilder()
                        .withName("Novel")
                        .withCategory("Books")
                        .build()
        );
        
        // When
        long electronicsCount = products.stream()
                .filter(p -> "Electronics".equals(p.getCategory()))
                .count();
        
        long booksCount = products.stream()
                .filter(p -> "Books".equals(p.getCategory()))
                .count();
        
        // Then
        assertThat(electronicsCount).isEqualTo(2);
        assertThat(booksCount).isEqualTo(2);
    }
}