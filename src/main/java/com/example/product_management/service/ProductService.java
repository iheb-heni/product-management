package com.example.productmanagement.service;

import com.example.productmanagement.dto.ProductRequest;
import com.example.productmanagement.dto.ProductResponse;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.exception.ValidationException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        // Vérifier si le produit existe déjà
        productRepository.findByName(request.getName())
                .ifPresent(product -> {
                    throw new ValidationException("Product with name '" + request.getName() + "' already exists");
                });
        
        // Mapper la requête vers l'entité
        Product product = modelMapper.map(request, Product.class);
        
        // Sauvegarder le produit
        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        
        return modelMapper.map(savedProduct, ProductResponse.class);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        return modelMapper.map(product, ProductResponse.class);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");
        
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Vérifier si le nouveau nom entre en conflit avec un produit existant
        if (!product.getName().equals(request.getName())) {
            productRepository.findByName(request.getName())
                    .ifPresent(p -> {
                        throw new ValidationException("Product with name '" + request.getName() + "' already exists");
                    });
        }
        
        // Mettre à jour le produit
        modelMapper.map(request, product);
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated with ID: {}", updatedProduct.getId());
        
        return modelMapper.map(updatedProduct, ProductResponse.class);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        log.debug("Fetching products by category: {}", category);
        
        return productRepository.findByCategory(category).stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(Integer threshold) {
        log.debug("Fetching low stock products with threshold: {}", threshold);
        
        return productRepository.findLowStockProducts(threshold).stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        log.debug("Searching products with keyword: {}", keyword);
        
        return productRepository.searchProducts(keyword).stream()
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }
}