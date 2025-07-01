package com.iot.demo.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.product.Product;
import com.iot.demo.product.ProductService;
import com.iot.demo.product.ProductStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeviceControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductService productService;

    private String productId;

    @BeforeEach
    void setupProduct() {
        Product product = new Product();
        product.setName("测试产品");
        product.setStatus(ProductStatusEnum.DEV);
        productService.create(product);
        productId = product.getId();
    }

    @Test
    void testCreateActivateDisableDevice() throws Exception {
        Device device = new Device();
        device.setName("测试设备");
        device.setDeviceSecret("secret-123");
        device.setProductId(productId);
        device.setStatus(DeviceStatusEnum.UNACTIVE);
        String json = objectMapper.writeValueAsString(device);
        // 创建
        String response = mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Device created = objectMapper.readValue(response, Device.class);
        // 启用
        mockMvc.perform(put("/api/devices/" + created.getId() + "/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENABLED"));
        // 禁用
        mockMvc.perform(put("/api/devices/" + created.getId() + "/disable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
    }
}