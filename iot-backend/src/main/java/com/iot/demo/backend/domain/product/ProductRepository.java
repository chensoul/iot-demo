package com.iot.demo.backend.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findByProductKey(String productKey);
    List<Product> findByCategoryId(Long categoryId);
}
