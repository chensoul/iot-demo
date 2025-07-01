package com.iot.demo.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThingModelRepository extends JpaRepository<ThingModel, Long> {
    List<ThingModel> findByProductId(String productId);
}
