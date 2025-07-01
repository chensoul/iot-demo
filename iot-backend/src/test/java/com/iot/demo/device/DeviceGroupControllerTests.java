package com.iot.demo.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.demo.backend.domain.device.Device;
import com.iot.demo.backend.domain.device.DeviceGroup;
import com.iot.demo.backend.domain.device.DeviceGroupRelation;
import com.iot.demo.backend.domain.product.Product;
import com.iot.demo.backend.domain.product.ProductRepository;
import com.iot.demo.backend.domain.product.ProductStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeviceGroupControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    private String productId;

    @BeforeEach
    void setupProduct() {
        Product product = new Product();
        product.setName("测试产品");
        product.setProductKey(UUID.randomUUID().toString().substring(0, 10));
        product.setProductSecret(UUID.randomUUID().toString().substring(0, 20));
        product.setStatus(ProductStatusEnum.DEV);
        productRepository.save(product);
        productId = product.getId();
    }

    @Test
    void testGroupTreeWithDevices() throws Exception {
        // 创建分组
        DeviceGroup root = new DeviceGroup();
        root.setName("根分组");
        String rootJson = objectMapper.writeValueAsString(root);
        String rootResp = mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rootJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        DeviceGroup rootCreated = objectMapper.readValue(rootResp, DeviceGroup.class);

        DeviceGroup child = new DeviceGroup();
        child.setName("子分组");
        child.setParentId(rootCreated.getId());
        String childJson = objectMapper.writeValueAsString(child);
        String childResp = mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(childJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        DeviceGroup childCreated = objectMapper.readValue(childResp, DeviceGroup.class);

        // 创建设备
        Device device = new Device();
        device.setName("测试设备");
        device.setDeviceSecret("secret-123");
        device.setProductId(productId);
        String deviceJson = objectMapper.writeValueAsString(device);
        String deviceResp = mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deviceJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Device createdDevice = objectMapper.readValue(deviceResp, Device.class);

        // 设备分配到子分组
        mockMvc.perform(post("/api/device-group-relations")
                        .param("deviceId", createdDevice.getId())
                        .param("groupId", childCreated.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        // 查询分组树+设备
        mockMvc.perform(get("/api/device-groups/tree-with-devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].children[0].devices[0].name").value("测试设备"));
    }

    @Test
    void testDeviceAssignAndRemoveFromGroup() throws Exception {
        // 创建分组
        DeviceGroup group = new DeviceGroup();
        group.setName("分组A");
        String groupJson = objectMapper.writeValueAsString(group);
        String groupResp = mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(groupJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        DeviceGroup createdGroup = objectMapper.readValue(groupResp, DeviceGroup.class);

        // 创建设备
        Device device = new Device();
        device.setName("设备A");
        device.setDeviceSecret("secret-456");
        device.setProductId(productId);
        String deviceJson = objectMapper.writeValueAsString(device);
        String deviceResp = mockMvc.perform(post("/api/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deviceJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Device createdDevice = objectMapper.readValue(deviceResp, Device.class);

        // 设备分配到分组
        String assignResp = mockMvc.perform(post("/api/device-group-relations")
                        .param("deviceId", createdDevice.getId())
                        .param("groupId", createdGroup.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        DeviceGroupRelation relation = objectMapper.readValue(assignResp, DeviceGroupRelation.class);

        // 查询设备所属分组
        mockMvc.perform(get("/api/device-group-relations/device/" + createdDevice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupId").value(createdGroup.getId()));

        // 移除设备分组
        mockMvc.perform(delete("/api/device-group-relations")
                        .param("deviceId", createdDevice.getId())
                        .param("groupId", createdGroup.getId().toString()))
                .andExpect(status().isOk());

        // 查询设备所属分组应为空
        mockMvc.perform(get("/api/device-group-relations/device/" + createdDevice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}