package com.iot.demo.thingmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ThingModelTemplateRepository extends JpaRepository<ThingModelTemplate, String> {
    Optional<ThingModelTemplate> findByTemplateKey(String templateKey);

    List<ThingModelTemplate> findByCategoryId(String categoryId);

    List<ThingModelTemplate> findByStatus(TemplateStatusEnum status);

    Optional<ThingModelTemplate> findFirstByCategoryIdAndStatusOrderByVersionDesc(String categoryId, TemplateStatusEnum status);

    @Query("SELECT t FROM ThingModelTemplate t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword% OR t.tags LIKE %:keyword%")
    List<ThingModelTemplate> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT t FROM ThingModelTemplate t ORDER BY t.usageCount DESC")
    List<ThingModelTemplate> findPopularTemplates();

    @Query("SELECT t FROM ThingModelTemplate t WHERE t.status = 'PUBLISHED' ORDER BY t.createTime DESC")
    List<ThingModelTemplate> findLatestPublishedTemplates();
}
