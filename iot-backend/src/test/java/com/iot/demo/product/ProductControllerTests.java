package com.iot.demo.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.backend.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateProductSuccess() throws Exception {
        Product product = new Product();
        product.setName("测试产品");
        product.setCategoryId(null); // 无分类
        String json = objectMapper.writeValueAsString(product);
        MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String resp = result.getResponse().getContentAsString();
        Product created = objectMapper.readValue(resp, Product.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("测试产品");
    }

    @Test
    void testCreateProductWithInvalidCategory() throws Exception {
        Product product = new Product();
        product.setName("无效分类产品");
        product.setCategoryId(-1L);
        String json = objectMapper.writeValueAsString(product);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testCreateProductWithCategory() throws Exception {
        // 先创建一个分类
        String catJson = "{\"name\":\"测试分类\"}";
        MvcResult catResult = mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(catJson))
                .andExpect(status().isOk())
                .andReturn();
        String catResp = catResult.getResponse().getContentAsString();
        JsonNode catNode = objectMapper.readTree(catResp);
        Long catId = catNode.get("id").asLong();

        Product product = new Product();
        product.setName("有分类产品");
        product.setCategoryId(catId);
        String json = objectMapper.writeValueAsString(product);
        MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();
        String resp = result.getResponse().getContentAsString();
        Product created = objectMapper.readValue(resp, Product.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getCategoryId()).isEqualTo(catId);
    }

    @Test
    void testCreateProductMissingName() throws Exception {
        Product product = new Product();
        String json = objectMapper.writeValueAsString(product);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }
}
