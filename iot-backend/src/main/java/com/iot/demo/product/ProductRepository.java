package com.iot.demo.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByProductKey(String productKey);

    List<Product> findByCategoryId(String categoryId);
}
