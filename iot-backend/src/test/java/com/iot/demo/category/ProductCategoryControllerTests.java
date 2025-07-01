package com.iot.demo.category;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
public class ProductCategoryControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetCategory() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setName("测试分类");
        category.setDescription("描述");
        String json = objectMapper.writeValueAsString(category);
        // 创建
        String response = mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        ProductCategory created = objectMapper.readValue(response, ProductCategory.class);
        // 查询
        mockMvc.perform(get("/api/product-categories/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()));
    }

    @Test
    void testUpdateCategory() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setName("初始分类");
        String json = objectMapper.writeValueAsString(category);
        String response = mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ProductCategory created = objectMapper.readValue(response, ProductCategory.class);
        // 更新
        created.setName("更新分类");
        String updateJson = objectMapper.writeValueAsString(created);
        mockMvc.perform(put("/api/product-categories/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("更新分类"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        ProductCategory category = new ProductCategory();
        category.setName("待删除分类");
        String json = objectMapper.writeValueAsString(category);
        String response = mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ProductCategory created = objectMapper.readValue(response, ProductCategory.class);
        // 删除
        mockMvc.perform(delete("/api/product-categories/" + created.getId()))
                .andExpect(status().isOk());
        // 查询应不存在
        mockMvc.perform(get("/api/product-categories/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testGetAllAndSearch() throws Exception {
        // 新建
        ProductCategory category = new ProductCategory();
        category.setName("搜索分类");
        String json = objectMapper.writeValueAsString(category);
        mockMvc.perform(post("/api/product-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
        // 查询全部
        mockMvc.perform(get("/api/product-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
        // 搜索
        mockMvc.perform(get("/api/product-categories/search?keyword=搜索"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("搜索分类"));
    }
}
