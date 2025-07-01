package com.iot.demo.category;

import com.iot.demo.product.TemplateStatusEnum;
import com.iot.demo.product.tsl.TslJsonUtils;
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
    private final ProductCategoryService productCategoryService;

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate create(Long categoryId, ThingModelTemplate.ThingModelTemplateUpdate update) {
        TslJsonUtils.parseTsl(update.getTemplateData());

        if (productCategoryService.getById(categoryId).isEmpty()) {
            throw new IllegalArgumentException("分类ID不存在");
        }

        ThingModelTemplate template = new ThingModelTemplate();
        template.setName(update.getName());
        template.setDescription(update.getDescription());
        template.setCategoryId(categoryId);
        template.setTemplateData(update.getTemplateData());
        template.setUsageCount(0);
        template.setStatus(TemplateStatusEnum.DRAFT);
        return templateRepository.save(template);
    }


    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate update(Long id, ThingModelTemplate.ThingModelTemplateUpdate update) {
        TslJsonUtils.parseTsl(update.getTemplateData());

        ThingModelTemplate template = getById(id).orElseThrow(() -> new IllegalArgumentException("物模型模版不存在"));
        template.setName(update.getName());
        template.setDescription(update.getDescription());
        template.setTemplateData(update.getTemplateData());

        return templateRepository.save(template);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        templateRepository.deleteById(id);
    }

    public Optional<ThingModelTemplate> getById(Long id) {
        return templateRepository.findById(id);
    }

    public List<ThingModelTemplate> list() {
        return templateRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModelTemplate publish(Long id) {
        Optional<ThingModelTemplate> opt = templateRepository.findById(id);
        if (opt.isPresent()) {
            ThingModelTemplate template = opt.get();
            List<ThingModelTemplate> sameCategoryPublished = templateRepository.findByCategoryId(template.getCategoryId());
            for (ThingModelTemplate t : sameCategoryPublished) {
                if (!t.getId().equals(template.getId()) && t.getStatus() == TemplateStatusEnum.RELEASE) {
                    t.setStatus(TemplateStatusEnum.DRAFT);
                    templateRepository.save(t);
                }
            }
            template.setStatus(TemplateStatusEnum.RELEASE);
            template.setPublishTime(LocalDateTime.now());
            return templateRepository.save(template);
        }
        throw new IllegalArgumentException("模板不存在");
    }

    public List<ThingModelTemplate> getByCategoryIdAndStatus(Long categoryId, TemplateStatusEnum status) {
        return templateRepository.findByCategoryIdAndStatus(categoryId, status);
    }
}
