package com.iot.demo.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    List<ProductCategory> findByParentId(String parentId);

    List<ProductCategory> findByNameContaining(String keyword);

    boolean existsByName(String name);

    boolean existsById(String id);

    boolean existsByNameAndIdNot(String name, String id);
}
