package com.iot.demo.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.category.ProductCategoryService;
import com.iot.demo.thingmodel.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ThingModelTemplateRepository templateRepository;
    private final ThingModelPropertyRepository propertyRepository;
    private final ThingModelServiceRepository serviceRepository;
    private final ThingModelEventRepository eventRepository;
    private final ThingModelParameterRepository parameterRepository;
    private final ProductCategoryService productCategoryService;

    public List<Product> list() {
        return productRepository.findAll();
    }

    public List<Product> listByCategoryId(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product create(Product product) {
        // 唯一性校验
        if (!StringUtils.isEmpty(product.getCategoryId()) && productCategoryService.getById(product.getCategoryId()).isEmpty()) {
            throw new IllegalArgumentException("分类ID不存在");
        }

        product.setProductKey(generateRandomKey(10));
        Product saved = productRepository.save(product);

        // 如果所选品类有发布状态的模板，则自动复制物模型数据
        templateRepository.findFirstByCategoryIdAndStatusOrderByVersionDesc(product.getCategoryId(), TemplateStatusEnum.PUBLISHED)
                .ifPresent(template -> {
                    try {
                        JsonNode root = new ObjectMapper().readTree(template.getTemplateData());
                        // 属性
                        if (root.has("properties")) {
                            for (JsonNode node : root.get("properties")) {
                                ThingModelProperty prop = new ThingModelProperty();
                                prop.setThingModelId(saved.getId());
                                prop.setIdentifier(node.path("identifier").asText());
                                prop.setName(node.path("name").asText());
                                prop.setDataType(DataTypeEnum.valueOf(node.path("dataType").asText()));
                                prop.setUnit(node.path("unit").asText(null));
                                prop.setAccessMode(AccessModeEnum.valueOf(node.path("accessMode").asText()));
                                prop.setDescription(node.path("description").asText(null));
                                prop.setRequired(node.path("required").asBoolean(false));
                                prop.setDefaultValue(node.path("defaultValue").asText(null));
                                propertyRepository.save(prop);
                            }
                        }
                        // 服务
                        if (root.has("services")) {
                            for (JsonNode node : root.get("services")) {
                                ThingModelService svc = new ThingModelService();
                                svc.setThingModelId(saved.getId());
                                svc.setIdentifier(node.path("identifier").asText());
                                svc.setName(node.path("name").asText());
                                svc.setCallType(CallTypeEnum.valueOf(node.path("callType").asText()));
                                svc.setDescription(node.path("description").asText(null));
                                serviceRepository.save(svc);
                            }
                        }
                        // 事件
                        if (root.has("events")) {
                            for (JsonNode node : root.get("events")) {
                                ThingModelEvent evt = new ThingModelEvent();
                                evt.setThingModelId(saved.getId());
                                evt.setIdentifier(node.path("identifier").asText());
                                evt.setName(node.path("name").asText());
                                evt.setType(EventTypeEnum.valueOf(node.path("type").asText()));
                                evt.setDescription(node.path("description").asText(null));
                                eventRepository.save(evt);
                            }
                        }
                        // 参数
                        if (root.has("parameters")) {
                            for (JsonNode node : root.get("parameters")) {
                                ThingModelParameter param = new ThingModelParameter();
                                param.setOwnerType(OwnerTypeEnum.valueOf(node.path("ownerType").asText()));
                                param.setOwnerId(node.path("ownerId").asText());
                                param.setIdentifier(node.path("identifier").asText());
                                param.setName(node.path("name").asText());
                                param.setDataType(DataTypeEnum.valueOf(node.path("dataType").asText()));
                                param.setUnit(node.path("unit").asText(null));
                                param.setDescription(node.path("description").asText(null));
                                param.setRequired(node.path("required").asBoolean(false));
                                param.setDirection(DirectionEnum.valueOf(node.path("direction").asText()));
                                param.setMinValue(node.path("minValue").asText(null));
                                param.setMaxValue(node.path("maxValue").asText(null));
                                param.setStep(node.path("step").asText(null));
                                if (node.has("maxLength")) param.setMaxLength(node.path("maxLength").asInt());
                                param.setPattern(node.path("pattern").asText(null));
                                param.setEnumOptions(node.path("enumOptions").asText(null));
                                param.setParentId(node.path("parentId").asText(null));
                                parameterRepository.save(param);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("模板物模型结构解析失败: " + e.getMessage(), e);
                    }
                });
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product update(String id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        if (!StringUtils.isEmpty(product.getCategoryId()) && productCategoryService.getById(product.getCategoryId()).isEmpty()) {
            throw new IllegalArgumentException("分类ID不存在");
        }
        product.setId(id);
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private String generateRandomKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
