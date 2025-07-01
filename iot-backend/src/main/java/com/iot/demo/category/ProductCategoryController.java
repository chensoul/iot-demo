package com.iot.demo.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {
    private final ProductCategoryService service;

    @PostMapping
    public ProductCategory create(@RequestBody @Valid ProductCategory category) {
        return service.create(category);
    }

    @PutMapping("/{id}")
    public ProductCategory update(@PathVariable String id, @RequestBody @Valid ProductCategory category) {
        return service.update(id, category);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping
    public List<ProductCategory> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ProductCategory getById(@PathVariable String id) {
        return service.getById(id).orElse(null);
    }

    @GetMapping("/parent/{parentId}")
    public List<ProductCategory> getByParent(@PathVariable String parentId) {
        return service.getByParent(parentId);
    }

    @GetMapping("/search")
    public List<ProductCategory> search(@RequestParam String keyword) {
        return service.search(keyword);
    }
}
