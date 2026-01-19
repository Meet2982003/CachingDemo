package com.cache.redis.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cache.redis.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {

}
