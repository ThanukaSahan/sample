package com.example.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sample.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
