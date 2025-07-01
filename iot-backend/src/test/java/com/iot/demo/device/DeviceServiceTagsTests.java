package com.iot.demo.device;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Transactional
@Rollback
class DeviceServiceTagsTests {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void testDeviceTagsCRUDAndQuery() {
        // 新建设备，带唯一前缀
        String unique = "test";
        final Device device = new Device();
        device.setName(unique);
        device.setDeviceSecret("secret");
        device.setProductId("product-1");
        device.setStatus(DeviceStatusEnum.ACTIVE);
        deviceRepository.save(device);

        // 批量添加标签
        Map<String, String> tags = new HashMap<>();
        tags.put("a", "1");
        tags.put("b", "2");
        deviceService.updateTags(device.getId(), tags);

        // 查询标签
        Map<String, String> got = deviceService.getTags(device.getId());
        Assertions.assertEquals("1", got.get("a"));
        Assertions.assertEquals("2", got.get("b"));

        // 按标签批量查询
        Map<String, String> queryTags = new HashMap<>();
        queryTags.put("a", "1");
        queryTags.put("b", "2");
        List<Device> found = deviceService.findByTags(queryTags);
        Assertions.assertTrue(found.stream().anyMatch(d -> d.getId().equals(device.getId())));

        // 更新标签
        tags.put("a", "3");
        deviceService.updateTags(device.getId(), tags);
        got = deviceService.getTags(device.getId());
        Assertions.assertEquals("3", got.get("a"));
        Assertions.assertEquals("2", got.get("b"));
    }
} 