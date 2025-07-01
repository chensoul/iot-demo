package com.iot.demo.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.category.ProductCategory;
import com.iot.demo.category.ProductCategoryRepository;
import com.iot.demo.thingmodel.TemplateStatusEnum;
import com.iot.demo.thingmodel.ThingModelTemplate;
import com.iot.demo.thingmodel.ThingModelTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Autowired
    private ThingModelTemplateRepository thingModelTemplateRepository;

    @Test
    void testCreateAndGetProduct() throws Exception {
        Product product = new Product();
        product.setName("测试产品");
        product.setNodeType(ProductNodeTypeEnum.DIRECT);
        product.setAuthType(ProductAuthTypeEnum.SECRET);
        product.setNetType(ProductNetTypeEnum.WIFI);
        product.setDataFormat(ProductDataFormatEnum.JSON);
        product.setDescription("测试产品描述");
        product.setProtocolType(ProductProtocolTypeEnum.MQTT);
        product.setStatus(ProductStatusEnum.ENABLED);
        product.setLogo("http://example.com/logo.png");

        // 创建产品
        String content = objectMapper.writeValueAsString(product);
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试产品"))
                .andReturn().getResponse().getContentAsString();
        Product created = objectMapper.readValue(response, Product.class);

        // 查询产品
        mockMvc.perform(get("/api/products/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("测试产品"));
    }

    @Test
    void testUpdateAndDeleteProduct() throws Exception {
        Product product = new Product();
        product.setName("待更新产品");
        product.setNodeType(ProductNodeTypeEnum.DIRECT);
        product.setAuthType(ProductAuthTypeEnum.SECRET);
        product.setNetType(ProductNetTypeEnum.WIFI);
        product.setDataFormat(ProductDataFormatEnum.JSON);
        product.setDescription("待更新产品描述");
        product.setProtocolType(ProductProtocolTypeEnum.MQTT);
        product.setStatus(ProductStatusEnum.ENABLED);
        product.setLogo("http://example.com/logo.png");
        String content = objectMapper.writeValueAsString(product);
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().getResponse().getContentAsString();
        Product created = objectMapper.readValue(response, Product.class);

        // 更新
        created.setName("已更新产品");
        mockMvc.perform(put("/api/products/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("已更新产品"));

        // 删除
        mockMvc.perform(delete("/api/products/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProductWithTemplate() throws Exception {
        // 1. 创建物模型模板
        // 构造模板物模型结构对象
        String modelData = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
            put("properties", java.util.Arrays.asList(new java.util.HashMap<String, Object>() {{
                put("identifier", "temp");
                put("name", "温度");
                put("dataType", "FLOAT");
                put("unit", "℃");
                put("accessMode", "R");
                put("description", "温度值");
                put("required", true);
            }}));
            put("services", java.util.Arrays.asList(new java.util.HashMap<String, Object>() {{
                put("identifier", "setTemp");
                put("name", "设置温度");
                put("callType", "SYNC");
                put("description", "设置温度服务");
            }}));
            put("events", java.util.Arrays.asList(new java.util.HashMap<String, Object>() {{
                put("identifier", "overheat");
                put("name", "过热");
                put("type", "ALARM");
                put("description", "过热告警");
            }}));
            put("parameters", java.util.Arrays.asList(new java.util.HashMap<String, Object>() {{
                put("ownerType", "SERVICE");
                put("ownerId", "setTemp");
                put("identifier", "target");
                put("name", "目标温度");
                put("dataType", "FLOAT");
                put("unit", "℃");
                put("description", "目标温度值");
                put("required", true);
                put("direction", "IN");
            }}));
        }});
        // 动态插入分类
        ProductCategory cat = new ProductCategory();
        cat.setName("测试分类");
        categoryRepository.saveAndFlush(cat);

        ThingModelTemplate template = new ThingModelTemplate();
        template.setTemplateKey("tpl001");
        template.setName("测试模板描述");
        template.setCategoryId(cat.getId());
        template.setTemplateData(modelData);
        template.setStatus(TemplateStatusEnum.PUBLISHED);
        thingModelTemplateRepository.saveAndFlush(template);

        // 2. 用模板创建产品
        Product product = new Product();
        product.setProductKey("key102");
        product.setName("模板产品");
        product.setNodeType(ProductNodeTypeEnum.DIRECT);
        product.setAuthType(ProductAuthTypeEnum.SECRET);
        product.setNetType(ProductNetTypeEnum.WIFI);
        product.setDataFormat(ProductDataFormatEnum.JSON);
        product.setDescription("模板产品描述");
        product.setCategoryId(cat.getId());
        product.setProtocolType(ProductProtocolTypeEnum.MQTT);
        product.setStatus(ProductStatusEnum.ENABLED);
        product.setLogo("http://example.com/logo.png");
        String content = objectMapper.writeValueAsString(product);
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Product created = objectMapper.readValue(response, Product.class);

        // 3. 验证物模型属性已被复制
        String propResp = mockMvc.perform(get("/api/thing-models/" + created.getId() + "/properties"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode propArr = objectMapper.readTree(propResp);
        boolean found = false;
        for (JsonNode node : propArr) {
            if ("temp".equals(node.get("identifier").asText()) && "温度".equals(node.get("name").asText())) {
                found = true;
                break;
            }
        }
        assert found;
    }
}
