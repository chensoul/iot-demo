package com.iot.demo.thingmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThingModelTemplateService {
    private final ThingModelTemplateRepository templateRepository;

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate create(ThingModelTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setUsageCount(0);
        template.setStatus(TemplateStatusEnum.DRAFT);
        return templateRepository.save(template);
    }

    // 复制模板时递增版本号
    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate copy(String id) {
        ThingModelTemplate origin = templateRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("模板不存在"));
        ThingModelTemplate copy = new ThingModelTemplate();
        copy.setCategoryId(origin.getCategoryId());
        copy.setName(origin.getName() + "_copy");
        copy.setDescription(origin.getDescription());
        copy.setAuthor(origin.getAuthor());
        copy.setTags(origin.getTags());
        copy.setTemplateData(origin.getTemplateData());
        copy.setCreateTime(LocalDateTime.now());
        copy.setUpdateTime(LocalDateTime.now());
        copy.setUsageCount(0);
        // 自动递增版本号
        List<ThingModelTemplate> templates = templateRepository.findByCategoryId(origin.getCategoryId());
        copy.setStatus(TemplateStatusEnum.DRAFT);
        return templateRepository.save(copy);
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate update(String id, ThingModelTemplate update) {
        update.setId(id);
        update.setUpdateTime(LocalDateTime.now());
        return templateRepository.save(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        templateRepository.deleteById(id);
    }

    public Optional<ThingModelTemplate> getById(String id) {
        return templateRepository.findById(id);
    }

    public Optional<ThingModelTemplate> getByKey(String templateKey) {
        return templateRepository.findByTemplateKey(templateKey);
    }

    public List<ThingModelTemplate> list() {
        return templateRepository.findAll();
    }

    public List<ThingModelTemplate> search(String keyword) {
        return templateRepository.searchByKeyword(keyword);
    }

    public List<ThingModelTemplate> listByCategoryId(String categoryId) {
        return templateRepository.findByCategoryId(categoryId);
    }

    public List<ThingModelTemplate> listByStatus(TemplateStatusEnum status) {
        return templateRepository.findByStatus(status);
    }

    public List<ThingModelTemplate> listPopular() {
        return templateRepository.findPopularTemplates();
    }

    public List<ThingModelTemplate> getLatestPublishedTemplates() {
        return templateRepository.findLatestPublishedTemplates();
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate publishTemplate(String id) {
        Optional<ThingModelTemplate> opt = templateRepository.findById(id);
        if (opt.isPresent()) {
            ThingModelTemplate template = opt.get();
            // 1. 先将同品类下其他PUBLISHED模板设为DEPRECATED
            List<ThingModelTemplate> sameCategoryPublished = templateRepository.findByCategoryId(template.getCategoryId());
            for (ThingModelTemplate t : sameCategoryPublished) {
                if (!t.getId().equals(template.getId()) && t.getStatus() == TemplateStatusEnum.PUBLISHED) {
                    t.setStatus(TemplateStatusEnum.DEPRECATED);
                    templateRepository.save(t);
                }
            }
            // 2. 设置当前模板为PUBLISHED
            template.setStatus(TemplateStatusEnum.PUBLISHED);
            template.setPublishTime(LocalDateTime.now());
            return templateRepository.save(template);
        }
        throw new IllegalArgumentException("模板不存在");
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate deprecateTemplate(String id) {
        Optional<ThingModelTemplate> opt = templateRepository.findById(id);
        if (opt.isPresent()) {
            ThingModelTemplate template = opt.get();
            template.setStatus(TemplateStatusEnum.DEPRECATED);
            return templateRepository.save(template);
        }
        throw new IllegalArgumentException("模板不存在");
    }
}
