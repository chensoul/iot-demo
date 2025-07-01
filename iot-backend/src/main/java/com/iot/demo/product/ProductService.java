package com.iot.demo.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.category.ProductCategoryService;
import com.iot.demo.category.ThingModelTemplateRepository;
import com.iot.demo.product.tsl.TslDTO;
import com.iot.demo.product.tsl.TslJsonUtils;
import com.iot.demo.util.NanoIdUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ThingModelTemplateRepository templateRepository;
    private final ProductCategoryService productCategoryService;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;
    private final ThingModelService thingModelService;

    public List<Product> listByCategoryId(Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product create(Product product) {
        product.setProductKey(NanoIdUtils.randomNanoId(11));
        product.setProductSecret(NanoIdUtils.randomNanoId(16));
        product.setStatus(ProductStatusEnum.DEV);
        Product saved = productRepository.save(product);

        if (product.getCategoryId() != null) {
            if (productCategoryService.getById(product.getCategoryId()).isEmpty()) {
                throw new IllegalArgumentException("分类ID不存在");
            }
            templateRepository.findFirstByCategoryIdAndStatusOrderByVersionDesc(product.getCategoryId(), TemplateStatusEnum.RELEASE)
                    .ifPresent(template -> {
                        TslDTO tsl = TslJsonUtils.parseTsl(template.getTemplateData());
                        TslDTO result = new TslDTO();
                        result.setSchema(tsl.getSchema());
                        result.setProfile(tsl.getProfile());
                        result.setProperties(tsl.getProperties() == null ? null : tsl.getProperties().stream().filter(TslDTO.TslPropertyDTO::getRequired).toList());
                        result.setServices(tsl.getServices() == null ? null : tsl.getServices().stream().filter(TslDTO.TslServiceDTO::getRequired).toList());
                        result.setEvents(tsl.getEvents() == null ? null : tsl.getEvents().stream().filter(TslDTO.TslEventDTO::getRequired).toList());
                        ThingModel thingModel = new ThingModel();
                        thingModel.setProductId(saved.getId());
                        try {
                            thingModel.setTemplateData(objectMapper.writeValueAsString(result));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        thingModelService.create(saved.getId(), thingModel);
                    });
        }
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product update(String id, Product.ProductUpdate productUpdate) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setName(productUpdate.getName());
        product.setDescription(productUpdate.getDescription());
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product publish(String id) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setStatus(ProductStatusEnum.RELEASE);
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product revoke(String id) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setStatus(ProductStatusEnum.DEV);
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTags(String id, Map<String, String> tags) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        try {
            product.setTags(objectMapper.writeValueAsString(tags));
        } catch (Exception e) {
            throw new RuntimeException("标签序列化失败", e);
        }
        productRepository.save(product);
    }

    public Map<String, String> getTags(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        try {
            if (product.getTags() == null) {
                return java.util.Collections.emptyMap();
            }
            return objectMapper.readValue(product.getTags(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("标签反序列化失败", e);
        }
    }

    public List<Product> findByTags(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) return productRepository.findAll();
        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE 1=1 ");
        for (String key : tags.keySet()) {
            sql.append(" AND JSON_EXTRACT(tags, '$.").append(key).append("') = :").append(key);
        }
        Query query = entityManager.createNativeQuery(sql.toString(), Product.class);
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }
}
