package com.iot.demo.thingmodel;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/thing-model-templates")
public class ThingModelTemplateController {
    @Autowired
    private ThingModelTemplateService templateService;

    @PostMapping
    public ThingModelTemplate create(@RequestBody @Valid ThingModelTemplate template) {
        return templateService.create(template);
    }

    @PutMapping("/{id}")
    public ThingModelTemplate update(@PathVariable String id, @RequestBody @Valid ThingModelTemplate template) {
        return templateService.update(id, template);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        templateService.delete(id);
    }

    @GetMapping
    public List<ThingModelTemplate> list() {
        return templateService.list();
    }

    @GetMapping("/search")
    public List<ThingModelTemplate> search(@RequestParam String keyword) {
        return templateService.search(keyword);
    }

    @GetMapping("/categoryId/{categoryId}")
    public List<ThingModelTemplate> listByCategoryId(@PathVariable String categoryId) {
        return templateService.listByCategoryId(categoryId);
    }

    @GetMapping("/status/{status}")
    public List<ThingModelTemplate> listByStatus(@PathVariable TemplateStatusEnum status) {
        return templateService.listByStatus(status);
    }

    @GetMapping("/popular")
    public List<ThingModelTemplate> listPopular() {
        return templateService.listPopular();
    }

    @GetMapping("/latest")
    public List<ThingModelTemplate> getLatestPublished() {
        return templateService.getLatestPublishedTemplates();
    }

    @GetMapping("/{id}")
    public ThingModelTemplate getById(@PathVariable String id) {
        return templateService.getById(id).orElse(null);
    }

    @PostMapping("/{id}/publish")
    public ThingModelTemplate publish(@PathVariable String id) {
        return templateService.publishTemplate(id);
    }

    @PostMapping("/{id}/deprecate")
    public ThingModelTemplate deprecate(@PathVariable String id) {
        return templateService.deprecateTemplate(id);
    }

    @PostMapping("/{id}/copy")
    public ThingModelTemplate copy(@PathVariable String id) {
        return templateService.copy(id);
    }
}
