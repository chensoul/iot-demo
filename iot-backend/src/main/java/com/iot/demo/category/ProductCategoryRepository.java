package com.iot.demo.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByParentId(Long parentId);

    List<ProductCategory> findByNameContaining(String keyword);

    boolean existsByName(String name);

    boolean existsById(Long id);

    boolean existsByNameAndIdNot(String name, Long id);
}
