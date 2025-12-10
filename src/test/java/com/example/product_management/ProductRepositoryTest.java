package com.example.product_management;

import com.example.product_management.dto.ProductRequest;
import com.example.product_management.dto.ProductResponse;
import com.example.product_management.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Tests avec Stubs et Spies au lieu de Mocks classiques
class ProductServiceWithStubsTest {
    
    // Implémentation en mémoire pour les tests
    static class InMemoryProductRepository {
        private final List<Product> products = new ArrayList<>();
        private Long currentId = 1L;
        
        public Product save(Product product) {
            if (product.getId() == null) {
                product.setId(currentId++);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
            } else {
                product.setUpdatedAt(LocalDateTime.now());
            }
            products.add(product);
            return product;
        }
        
        public Optional<Product> findById(Long id) {
            return products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
        }
        
        public Optional<Product> findByName(String name) {
            return products.stream()
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst();
        }
        
        public List<Product> findAll() {
            return new ArrayList<>(products);
        }
        
        public List<Product> findByCategory(String category) {
            return products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .toList();
        }
        
        public boolean existsById(Long id) {
            return products.stream()
                    .anyMatch(p -> p.getId().equals(id));
        }
        
        public void deleteById(Long id) {
            products.removeIf(p -> p.getId().equals(id));
        }
        
        public void clear() {
            products.clear();
            currentId = 1L;
        }
    }
    
    // Stub pour ModelMapper
    static class TestModelMapper {
        public Product mapToEntity(ProductRequest request) {
            return Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .category(request.getCategory())
                    .build();
        }
        
        public ProductResponse mapToResponse(Product product) {
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .category(product.getCategory())
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();
        }
    }
    
    private InMemoryProductRepository repository;
    private TestModelMapper modelMapper;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        modelMapper = new TestModelMapper();
        repository.clear();
    }
    
    @Test
    void createProduct_Success() {
        // Given
        ProductRequest request = ProductRequest.builder()
                .name("Laptop")
                .description("Gaming laptop")
                .price(new BigDecimal("1999.99"))
                .quantity(5)
                .category("Electronics")
                .build();
        
        // When
        Product entity = modelMapper.mapToEntity(request);
        Product saved = repository.save(entity);
        ProductResponse response = modelMapper.mapToResponse(saved);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Laptop");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("1999.99"));
        assertThat(repository.findAll()).hasSize(1);
    }
    
    @Test
    void findProductById_Success() {
        // Given
        Product product = Product.builder()
                .name("Smartphone")
                .description("Latest model")
                .price(new BigDecimal("999.99"))
                .quantity(10)
                .category("Electronics")
                .build();
        
        Product saved = repository.save(product);
        
        // When
        Optional<Product> found = repository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Smartphone");
    }
    
    @Test
    void findProductById_NotFound() {
        // When
        Optional<Product> found = repository.findById(999L);
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void findProductByName_Success() {
        // Given
        Product product = Product.builder()
                .name("Tablet")
                .description("10-inch tablet")
                .price(new BigDecimal("499.99"))
                .quantity(8)
                .category("Electronics")
                .build();
        
        repository.save(product);
        
        // When
        Optional<Product> found = repository.findByName("Tablet");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("10-inch tablet");
    }
    
    @Test
    void findAllProducts_Empty() {
        // When
        List<Product> products = repository.findAll();
        
        // Then
        assertThat(products).isEmpty();
    }
    
    @Test
    void findAllProducts_WithData() {
        // Given
        repository.save(Product.builder()
                .name("Product 1")
                .description("Desc 1")
                .price(new BigDecimal("100.00"))
                .quantity(5)
                .category("Category1")
                .build());
        
        repository.save(Product.builder()
                .name("Product 2")
                .description("Desc 2")
                .price(new BigDecimal("200.00"))
                .quantity(10)
                .category("Category2")
                .build());
        
        // When
        List<Product> products = repository.findAll();
        
        // Then
        assertThat(products).hasSize(2);
        assertThat(products)
                .extracting(Product::getName)
                .containsExactly("Product 1", "Product 2");
    }
    
    @Test
    void findByCategory_Success() {
        // Given
        repository.save(Product.builder()
                .name("Electronics 1")
                .description("Desc 1")
                .price(new BigDecimal("100.00"))
                .quantity(5)
                .category("Electronics")
                .build());
        
        repository.save(Product.builder()
                .name("Electronics 2")
                .description("Desc 2")
                .price(new BigDecimal("200.00"))
                .quantity(10)
                .category("Electronics")
                .build());
        
        repository.save(Product.builder()
                .name("Book 1")
                .description("Desc 3")
                .price(new BigDecimal("50.00"))
                .quantity(15)
                .category("Books")
                .build());
        
        // When
        List<Product> electronics = repository.findByCategory("Electronics");
        
        // Then
        assertThat(electronics).hasSize(2);
        assertThat(electronics)
                .allMatch(p -> p.getCategory().equals("Electronics"));
    }
    
    @Test
    void deleteProduct_Success() {
        // Given
        Product product = Product.builder()
                .name("To Delete")
                .description("Will be deleted")
                .price(new BigDecimal("99.99"))
                .quantity(3)
                .category("Test")
                .build();
        
        Product saved = repository.save(product);
        
        // When
        repository.deleteById(saved.getId());
        
        // Then
        assertThat(repository.findAll()).isEmpty();
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}