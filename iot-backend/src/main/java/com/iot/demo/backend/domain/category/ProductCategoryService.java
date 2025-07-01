package com.iot.demo.backend.domain.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository repository;


    @Transactional(rollbackFor = Exception.class)
    public ProductCategory create(ProductCategory category) {
        if (repository.existsByName(category.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        // 父级分类存在性校验（如有父ID）
        if (category.getParentId() != null) {
            if (!repository.existsById(category.getParentId())) {
                throw new IllegalArgumentException("父级分类不存在");
            }
        }
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return repository.save(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductCategory update(Long id, ProductCategory update) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("分类ID不存在");
        }
        // 名称唯一性校验（排除自身）
        if (repository.existsByNameAndIdNot(update.getName(), id)) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        // 父级分类存在性校验（如有父ID）
        if (update.getParentId() != null) {
            if (!repository.existsById(update.getParentId())) {
                throw new IllegalArgumentException("父级分类不存在");
            }
        }
        update.setId(id);
        update.setUpdateTime(LocalDateTime.now());
        return repository.save(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<ProductCategory> list() {
        return repository.findAll();
    }

    public Optional<ProductCategory> getById(Long id) {
        return repository.findById(id);
    }

    public List<ProductCategory> getByParent(Long parentId) {
        return repository.findByParentId(parentId);
    }

    public List<ProductCategory> search(String keyword) {
        return repository.findByNameContaining(keyword);
    }

}
