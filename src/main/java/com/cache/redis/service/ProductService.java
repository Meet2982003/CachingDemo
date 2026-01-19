package com.cache.redis.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cache.redis.exceptions.ResourceNotFoundException;
import com.cache.redis.model.Product;
import com.cache.redis.repo.ProductRepo;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @CachePut(value = "products", key = "#product.id")
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

    @CachePut(value = "products", key = "#product.id")
    public List<Product> createAllProduct(List<Product> products) {
        return productRepo.saveAll(products);
    }

    @Cacheable(value = "products", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return Optional.of(productRepo.findById(id).orElse(null));
    }

    @Cacheable(value = "products", key = "'allProducts'")
    public Iterable<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @CachePut(value = "products", key = "#id")
    @CacheEvict(value = "products", key = "'allProducts'")
    public Optional<Object> updateProduct(Long id, Product productDetails) {
        return Optional.of(productRepo.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setQuantity(productDetails.getQuantity());
            return productRepo.save(product);
        }).orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id)));
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteAllProducts() {
        productRepo.deleteAll();
    }

    @CacheEvict(value = "products", allEntries = true)
    public void clearCache() {
        // This method will clear all entries in the 'products' cache
    }

}
