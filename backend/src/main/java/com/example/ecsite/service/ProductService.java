package com.example.ecsite.service;

import com.example.ecsite.dto.ProductResponse;
import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductResponse> searchProducts(String keyword, String category) {
        String normalizedKeyword = normalize(keyword);
        return productRepository.search(normalizedKeyword, category).stream()
                .map(ProductResponse::from)
                .toList();
    }

    public Optional<ProductResponse> getProductById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return productRepository.findById(id).map(ProductResponse::from);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(Objects.requireNonNull(product, "product must not be null"));
    }
}
