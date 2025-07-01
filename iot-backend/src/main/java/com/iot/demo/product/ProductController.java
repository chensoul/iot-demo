package com.iot.demo.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ThingModelService thingModelService;

    @GetMapping
    public List<Product> list(@RequestParam(required = false) Long categoryId) {
        return productService.listByCategoryId(categoryId);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return productService.getById(id).orElse(null);
    }

    @PostMapping
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @Valid @RequestBody Product.ProductUpdate productUpdate) {
        return productService.update(id, productUpdate);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }

    @PutMapping("/{id}/publish")
    public Product publish(@PathVariable String id) {
        return productService.publish(id);
    }

    @PutMapping("/{id}/revoke")
    public Product revoke(@PathVariable String id) {
        return productService.revoke(id);
    }

    @GetMapping("/{productId}/thing-models")
    public List<ThingModel> list(@PathVariable String productId) {
        return thingModelService.list(productId);
    }

    @PostMapping("/{productId}/thing-model")
    public ThingModel create(@PathVariable String productId, @Valid @RequestBody ThingModel property) {
        return thingModelService.create(productId, property);
    }

    @GetMapping("/{productId}/thing-model/{id}/tsl")
    public String getProductTsl(@PathVariable String productId, @PathVariable Long id) {
        return thingModelService.getById(id).map(ThingModel::getTemplateData).orElse(null);
    }

    @PutMapping("/{productId}/thing-model/{id}")
    public ThingModel update(@PathVariable String productId, @PathVariable Long id, @Valid @RequestBody ThingModel property) {
        return thingModelService.update(productId, id, property);
    }

    @DeleteMapping("/{productId}/thing-model/{id}")
    public void delete(@PathVariable String productId, @PathVariable Long id) {
        thingModelService.delete(productId, id);
    }

}
