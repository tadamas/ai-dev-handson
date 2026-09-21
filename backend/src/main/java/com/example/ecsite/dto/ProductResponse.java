package com.example.ecsite.dto;

import com.example.ecsite.entity.Product;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Integer price,
        String imageUrl,
        String category,
        Integer stock) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getStock());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public Integer getStock() { return stock; }
}
