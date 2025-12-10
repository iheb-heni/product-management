package com.example.productmanagement.controller;

import com.example.productmanagement.dto.ApiResponse;
import com.example.productmanagement.dto.ProductRequest;
import com.example.productmanagement.dto.ProductResponse;
import com.example.productmanagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Product already exists")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }
    
    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Product name already exists")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Product deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "Product category") @PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products, "Products retrieved successfully"));
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
            @Parameter(description = "Stock threshold", example = "10") 
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductResponse> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponse.success(products, "Low stock products retrieved successfully"));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products by keyword")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success(products, "Search results retrieved successfully"));
    }
}