package com.cache.redis.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cache.redis.model.Product;
import com.cache.redis.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return new ResponseEntity<Product>(savedProduct, HttpStatus.CREATED);
    }

    @PostMapping("create-all")
    public ResponseEntity<List<Product>> createAllProducts(@RequestBody List<Product> products) {
        List<Product> savedProducts = productService.createAllProduct(products);
        return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
    }

    @PostMapping("get-by-id/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("get-all")
    public ResponseEntity<Iterable<Product>> getAllProducts() {
        Iterable<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @PostMapping("update/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Object> updatedProduct = productService.updateProduct(id, productDetails);
        return updatedProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("delete-by-id/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("delete-all")
    public ResponseEntity<Void> deleteAllProducts() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("clear-cache")
    public ResponseEntity<Void> clearCache() {
        productService.clearCache();
        return ResponseEntity.noContent().build();
    }
}
