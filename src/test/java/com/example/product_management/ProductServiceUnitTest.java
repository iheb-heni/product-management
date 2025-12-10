package com.example.product_management;

import com.example.product_management.dto.ProductRequest;
import com.example.product_management.dto.ProductResponse;
import com.example.product_management.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {
    
    @Mock
    private com.example.product_management.repository.ProductRepository productRepository;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private com.example.product_management.service.ProductService productService;
    
    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;
    
    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();
        
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();
        
        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .quantity(10)
                .category("Electronics")
                .build();
    }
    
    @Test
    void testProductRequestCreation() {
        assertNotNull(productRequest);
        assertEquals("Test Product", productRequest.getName());
        assertEquals("Electronics", productRequest.getCategory());
        assertEquals(new BigDecimal("99.99"), productRequest.getPrice());
        assertEquals(10, productRequest.getQuantity());
    }
    
    @Test
    void testProductEntityCreation() {
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Test Description", product.getDescription());
    }
    
    @Test
    void testProductResponseCreation() {
        assertNotNull(productResponse);
        assertEquals(1L, productResponse.getId());
        assertEquals("Test Product", productResponse.getName());
        assertEquals(new BigDecimal("99.99"), productResponse.getPrice());
    }
    
    @Test
    void testServiceDependenciesInjected() {
        assertNotNull(productService);
        // Les mocks sont injectés automatiquement par @InjectMocks
    }
}