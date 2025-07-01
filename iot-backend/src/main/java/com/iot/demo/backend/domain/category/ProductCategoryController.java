package com.iot.demo.backend.domain.category;

import com.iot.demo.backend.domain.product.TemplateStatusEnum;
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
    private final ThingModelTemplateService templateService;

    @PostMapping
    public ProductCategory create(@RequestBody @Valid ProductCategory category) {
        return service.create(category);
    }

    @PutMapping("/{categoryId}")
    public ProductCategory update(@PathVariable Long categoryId, @RequestBody @Valid ProductCategory category) {
        return service.update(categoryId, category);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        service.delete(categoryId);
    }

    @GetMapping
    public List<ProductCategory> list() {
        return service.list();
    }

    @GetMapping("/{categoryId}")
    public ProductCategory getById(@PathVariable Long categoryId) {
        return service.getById(categoryId).orElse(null);
    }

    @GetMapping("/parent/{parentId}")
    public List<ProductCategory> getByParent(@PathVariable Long parentId) {
        return service.getByParent(parentId);
    }

    @GetMapping("/search")
    public List<ProductCategory> search(@RequestParam String keyword) {
        return service.search(keyword);
    }

    @GetMapping("/{categoryId}/template/release")
    public ThingModelTemplate getByCategoryIdAndStatus(@PathVariable Long categoryId) {
        return templateService.getByCategoryIdAndStatus(categoryId, TemplateStatusEnum.RELEASE).getFirst();
    }

    @GetMapping("/{categoryId}/template/draft")
    public List<ThingModelTemplate> listByCategoryIdAndStatus(@PathVariable Long categoryId) {
        return templateService.getByCategoryIdAndStatus(categoryId, TemplateStatusEnum.DRAFT);
    }

    @PostMapping("/{categoryId}/template")
    public ThingModelTemplate createTemplate(@PathVariable Long categoryId,
                                             @RequestBody @Valid ThingModelTemplate.ThingModelTemplateUpdate update) {
        return templateService.create(categoryId, update);
    }

    @PutMapping("/{categoryId}/template/{templateId}")
    public ThingModelTemplate updateTemplate(@PathVariable Long categoryId, @PathVariable Long templateId,
                                             @RequestBody @Valid ThingModelTemplate.ThingModelTemplateUpdate update) {
        return templateService.update(templateId, update);
    }

    @GetMapping("/{categoryId}/template/{templateId}")
    public ThingModelTemplate getTById(@PathVariable Long categoryId, Long templateId) {
        return templateService.getById(templateId).orElse(null);
    }

    @PostMapping("/{categoryId}/template/{templateId}/publish")
    public ThingModelTemplate publish(@PathVariable Long categoryId, @PathVariable Long templateId) {
        return templateService.publish(templateId);
    }
}
