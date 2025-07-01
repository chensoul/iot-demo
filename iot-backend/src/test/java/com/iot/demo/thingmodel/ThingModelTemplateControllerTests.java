package com.iot.demo.thingmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class ThingModelTemplateControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetTemplate() throws Exception {
        ThingModelTemplate template = new ThingModelTemplate();
        template.setName("测试模板");
        template.setCategoryId("test-category-id");
        template.setDescription("测试模板描述");
        template.setTemplateKey("test-key-1");
        template.setStatus(TemplateStatusEnum.DRAFT);
        template.setTemplateData("{}");

        // 创建模板
        ResultActions createResult = mockMvc.perform(post("/api/thing-model-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(template)));
        createResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试模板"));

        // 查询所有模板
        mockMvc.perform(get("/api/thing-model-templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("测试模板"));
    }

    @Test
    void testUpdateAndDeleteTemplate() throws Exception {
        // 先创建
        ThingModelTemplate template = new ThingModelTemplate();
        template.setName("待更新模板");
        template.setCategoryId("test-category-id");
        template.setDescription("待更新模板描述");
        template.setTemplateKey("test-key-2");
        template.setStatus(TemplateStatusEnum.DRAFT);
        template.setTemplateData("{}");
        String content = objectMapper.writeValueAsString(template);
        String response = mockMvc.perform(post("/api/thing-model-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().getResponse().getContentAsString();
        ThingModelTemplate created = objectMapper.readValue(response, ThingModelTemplate.class);

        // 更新
        created.setName("已更新模板");
        mockMvc.perform(put("/api/thing-model-templates/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("已更新模板"));

        // 删除
        mockMvc.perform(delete("/api/thing-model-templates/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testPublishAndDeprecateTemplate() throws Exception {
        // 创建
        ThingModelTemplate template = new ThingModelTemplate();
        template.setName("发布模板");
        template.setCategoryId("test-category-id");
        template.setDescription("发布模板描述");
        template.setTemplateKey("test-key-3");
        template.setStatus(TemplateStatusEnum.DRAFT);
        template.setTemplateData("{}");
        String content = objectMapper.writeValueAsString(template);
        String response = mockMvc.perform(post("/api/thing-model-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn().getResponse().getContentAsString();
        ThingModelTemplate created = objectMapper.readValue(response, ThingModelTemplate.class);

        // 发布
        mockMvc.perform(post("/api/thing-model-templates/" + created.getId() + "/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // 废弃
        mockMvc.perform(post("/api/thing-model-templates/" + created.getId() + "/deprecate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEPRECATED"));
    }
}
