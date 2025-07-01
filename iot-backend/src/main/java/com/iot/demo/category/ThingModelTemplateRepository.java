package com.iot.demo.category;

import com.iot.demo.product.TemplateStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThingModelTemplateRepository extends JpaRepository<ThingModelTemplate, Long> {
    List<ThingModelTemplate> findByCategoryId(Long categoryId);

    List<ThingModelTemplate> findByCategoryIdAndStatus(Long categoryId, TemplateStatusEnum status);

    Optional<ThingModelTemplate> findFirstByCategoryIdAndStatusOrderByVersionDesc(Long categoryId, TemplateStatusEnum status);
}
