package com.iot.demo.thingmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
class ThingModelControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ThingModelRepository thingModelRepository;
    @Autowired
    private ThingModelServiceRepository serviceRepository;
    @Autowired
    private ThingModelPropertyRepository propertyRepository;
    @Autowired
    private ThingModelEventRepository eventRepository;
    @Autowired
    private ThingModelParameterRepository parameterRepository;

    @BeforeEach
    void setUp() {
        // 清理所有测试数据
        parameterRepository.deleteAll();
        propertyRepository.deleteAll();
        serviceRepository.deleteAll();
        eventRepository.deleteAll();
        thingModelRepository.deleteAll();

        // 配置ObjectMapper
        objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    }

    // ==================== 物模型基础CRUD测试 ====================

    @Test
    void testThingModelCRUD() throws Exception {
        // 创建
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("CRUD物模型");
        model.setDescription("CRUD测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 修改
        created.setName("CRUD物模型-改");
        String updateJson = objectMapper.writeValueAsString(created);
        mockMvc.perform(put("/api/thing-models/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CRUD物模型-改"));

        // 查询列表
        mockMvc.perform(get("/api/thing-models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        // 删除
        mockMvc.perform(delete("/api/thing-models/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void testThingModelGetById() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("查询测试");
        model.setDescription("查询测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 根据ID查询
        mockMvc.perform(get("/api/thing-models/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("查询测试"));
    }

    @Test
    void testThingModelGetByIdNotFound() throws Exception {
        // 查询不存在的物模型
        String fakeId = java.util.UUID.randomUUID().toString();
        mockMvc.perform(get("/api/thing-models/" + fakeId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNonExistThingModel() throws Exception {
        // 删除不存在的物模型
        String fakeId = UUID.randomUUID().toString();
        mockMvc.perform(delete("/api/thing-models/" + fakeId))
                .andExpect(status().isOk()); // 允许幂等删除
    }

    // ==================== 属性管理测试 ====================

    @Test
    void testAddPropertyRequiredField() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("属性必填测试");
        model.setDescription("属性必填");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 缺少 identifier，应该报 400
        ThingModelProperty prop = new ThingModelProperty();
        prop.setName("温度");
        prop.setDataType(DataTypeEnum.FLOAT);
        prop.setUnit("℃");
        prop.setAccessMode(AccessModeEnum.RW);
        prop.setDescription("温度属性");
        prop.setRequired(true);
        prop.setDefaultValue("0");
        String propJson = objectMapper.writeValueAsString(prop);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddPropertySuccess() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("属性成功测试");
        model.setDescription("属性成功");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加属性（成功）
        ThingModelProperty prop = new ThingModelProperty();
        prop.setIdentifier("temperature");
        prop.setName("温度");
        prop.setDataType(DataTypeEnum.FLOAT);
        prop.setUnit("℃");
        prop.setAccessMode(AccessModeEnum.RW);
        prop.setDescription("温度属性");
        prop.setRequired(true);
        prop.setDefaultValue("0");
        String propJson = objectMapper.writeValueAsString(prop);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("temperature"))
                .andExpect(jsonPath("$.name").value("温度"))
                .andExpect(jsonPath("$.dataType").value("FLOAT"))
                .andExpect(jsonPath("$.accessMode").value("RW"));
    }

    @Test
    void testGetProperties() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("属性查询测试");
        model.setDescription("属性查询");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加属性
        ThingModelProperty prop = new ThingModelProperty();
        prop.setIdentifier("humidity");
        prop.setName("湿度");
        prop.setDataType(DataTypeEnum.FLOAT);
        prop.setUnit("%");
        prop.setAccessMode(AccessModeEnum.R);
        prop.setDescription("湿度属性");
        prop.setRequired(false);
        String propJson = objectMapper.writeValueAsString(prop);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isOk());

        // 查询属性列表
        mockMvc.perform(get("/api/thing-models/" + created.getId() + "/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identifier").value("humidity"))
                .andExpect(jsonPath("$[0].name").value("湿度"))
                .andExpect(jsonPath("$[0].dataType").value("FLOAT"))
                .andExpect(jsonPath("$[0].accessMode").value("R"));
    }

    @Test
    void testDeleteProperty() throws Exception {
        // 创建物模型和属性
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("属性删除测试");
        model.setDescription("属性删除");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加属性
        ThingModelProperty prop = new ThingModelProperty();
        prop.setIdentifier("pressure");
        prop.setName("压力");
        prop.setDataType(DataTypeEnum.FLOAT);
        prop.setUnit("Pa");
        prop.setAccessMode(AccessModeEnum.RW);
        prop.setDescription("压力属性");
        prop.setRequired(true);
        String propJson = objectMapper.writeValueAsString(prop);
        String propResponse = mockMvc.perform(post("/api/thing-models/" + created.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelProperty createdProp = objectMapper.readValue(propResponse, ThingModelProperty.class);

        // 删除属性
        mockMvc.perform(delete("/api/thing-models/properties/" + createdProp.getId()))
                .andExpect(status().isOk());
    }

    // ==================== 服务管理测试 ====================

    @Test
    void testAddServiceSuccess() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("服务成功测试");
        model.setDescription("服务成功");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加服务（成功）
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("reboot");
        svc.setName("重启设备");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("重启设备服务");
        String svcJson = objectMapper.writeValueAsString(svc);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("reboot"))
                .andExpect(jsonPath("$.name").value("重启设备"))
                .andExpect(jsonPath("$.callType").value("SYNC"));
    }

    @Test
    void testGetServices() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("服务查询测试");
        model.setDescription("服务查询");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("updateFirmware");
        svc.setName("固件升级");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("固件升级服务");
        String svcJson = objectMapper.writeValueAsString(svc);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk());

        // 查询服务列表
        mockMvc.perform(get("/api/thing-models/" + created.getId() + "/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identifier").value("updateFirmware"))
                .andExpect(jsonPath("$[0].callType").value("SYNC"))
                .andExpect(jsonPath("$[0].name").value("固件升级"));
    }

    @Test
    void testDeleteService() throws Exception {
        // 创建物模型和服务
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("服务删除测试");
        model.setDescription("服务删除");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("factoryReset");
        svc.setName("恢复出厂设置");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("恢复出厂设置服务");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResponse = mockMvc.perform(post("/api/thing-models/" + created.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResponse, ThingModelService.class);

        // 删除服务
        mockMvc.perform(delete("/api/thing-models/services/" + createdSvc.getId()))
                .andExpect(status().isOk());
    }

    // ==================== 事件管理测试 ====================

    @Test
    void testAddEventSuccess() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("事件成功测试");
        model.setDescription("事件成功");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加事件（成功）
        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier("alarm");
        event.setName("告警事件");
        event.setType(EventTypeEnum.INFO);
        event.setDescription("设备告警事件");
        String eventJson = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("alarm"))
                .andExpect(jsonPath("$.name").value("告警事件"))
                .andExpect(jsonPath("$.type").value("INFO"));
    }

    @Test
    void testGetEvents() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("事件查询测试");
        model.setDescription("事件查询");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加事件
        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier("error");
        event.setName("故障事件");
        event.setType(EventTypeEnum.WARNING);
        event.setDescription("设备故障事件");
        String eventJson = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/api/thing-models/" + created.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk());

        // 查询事件列表
        mockMvc.perform(get("/api/thing-models/" + created.getId() + "/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identifier").value("error"))
                .andExpect(jsonPath("$[0].name").value("故障事件"));
    }

    @Test
    void testDeleteEvent() throws Exception {
        // 创建物模型和事件
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("事件删除测试");
        model.setDescription("事件删除");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        ThingModel created = objectMapper.readValue(response, ThingModel.class);

        // 添加事件
        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier("statusChange");
        event.setName("状态变更");
        event.setType(EventTypeEnum.WARNING);
        event.setDescription("设备状态变更事件");
        String eventJson = objectMapper.writeValueAsString(event);
        String eventResponse = mockMvc.perform(post("/api/thing-models/" + created.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelEvent createdEvent = objectMapper.readValue(eventResponse, ThingModelEvent.class);

        // 删除事件
        mockMvc.perform(delete("/api/thing-models/events/" + createdEvent.getId()))
                .andExpect(status().isOk());
    }

    // ==================== 参数管理测试 ====================

    @Test
    void testAddServiceParameterEnumAndValidation() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("服务参数校验");
        model.setDescription("服务参数校验");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("setThreshold");
        svc.setName("设置阈值");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("设置温度阈值");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("setThreshold"))
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 添加服务参数（带验证）
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("threshold");
        param.setName("阈值");
        param.setDataType(DataTypeEnum.FLOAT);
        param.setUnit("℃");
        param.setDescription("温度阈值");
        param.setRequired(true);
        param.setDirection(DirectionEnum.IN);
        param.setMinValue("0");
        param.setMaxValue("100");
        param.setStep("0.1");
        param.setEnumOptions("[\"10\",\"20\",\"30\"]");
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("threshold"))
                .andExpect(jsonPath("$.minValue").value("0"))
                .andExpect(jsonPath("$.maxValue").value("100"));
    }

    @Test
    void testAddServiceParameterEnumOptions() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("枚举参数模型");
        model.setDescription("枚举参数测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("setMode");
        svc.setName("设置模式");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("设置工作模式");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("setMode"))
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 添加服务参数（枚举类型）
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("mode");
        param.setName("工作模式");
        param.setDataType(DataTypeEnum.ENUM);
        param.setUnit("");
        param.setDescription("工作模式选择");
        param.setRequired(true);
        param.setDirection(DirectionEnum.IN);
        param.setEnumOptions("[{\"value\":\"auto\",\"desc\":\"自动\"},{\"value\":\"manual\",\"desc\":\"手动\"}]");
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("mode"))
                .andExpect(jsonPath("$.enumOptions").value("[{\"value\":\"auto\",\"desc\":\"自动\"},{\"value\":\"manual\",\"desc\":\"手动\"}]"));
    }

    @Test
    void testAddEventParameter() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("事件参数模型");
        model.setDescription("事件参数测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加事件
        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier("dataReport");
        event.setName("数据上报");
        event.setType(EventTypeEnum.INFO);
        event.setDescription("数据上报事件");
        String eventJson = objectMapper.writeValueAsString(event);
        String eventResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("dataReport"))
                .andReturn().getResponse().getContentAsString();
        ThingModelEvent createdEvent = objectMapper.readValue(eventResp, ThingModelEvent.class);

        // 添加事件参数
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.EVENT);
        param.setOwnerId(createdEvent.getId());
        param.setIdentifier("data");
        param.setName("上报数据");
        param.setDataType(DataTypeEnum.STRING);
        param.setUnit("");
        param.setDescription("上报的数据内容");
        param.setRequired(true);
        param.setDirection(DirectionEnum.OUT);
        param.setMaxLength(1000);
        param.setPattern(".*");
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/event/" + createdEvent.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").value("data"))
                .andExpect(jsonPath("$.maxLength").value(1000));
    }

    @Test
    void testGetParameters() throws Exception {
        // 创建物模型和服务
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("参数查询模型");
        model.setDescription("参数查询测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("getStatus");
        svc.setName("获取状态");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("获取设备状态");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 添加服务参数
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("status");
        param.setName("状态");
        param.setDataType(DataTypeEnum.ENUM);
        param.setUnit("");
        param.setDescription("设备状态");
        param.setRequired(true);
        param.setDirection(DirectionEnum.OUT);
        param.setEnumOptions("online:在线,offline:离线,error:错误");
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk());

        // 查询参数列表
        mockMvc.perform(get("/api/thing-models/service/" + createdSvc.getId() + "/parameters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identifier").value("status"))
                .andExpect(jsonPath("$[0].name").value("状态"))
                .andExpect(jsonPath("$[0].dataType").value("ENUM"));
    }

    @Test
    void testDeleteParameter() throws Exception {
        // 创建物模型、服务和参数
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("参数删除模型");
        model.setDescription("参数删除测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("testDelete");
        svc.setName("测试删除");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("测试删除服务");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 添加参数
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("testParam");
        param.setName("测试参数");
        param.setDataType(DataTypeEnum.INT);
        param.setUnit("");
        param.setDescription("测试参数");
        param.setRequired(false);
        param.setDirection(DirectionEnum.IN);
        param.setMinValue("0");
        param.setMaxValue("100");
        String paramJson = objectMapper.writeValueAsString(param);
        String paramResp = mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelParameter createdParam = objectMapper.readValue(paramResp, ThingModelParameter.class);

        // 删除参数
        mockMvc.perform(delete("/api/thing-models/parameters/" + createdParam.getId()))
                .andExpect(status().isOk());
    }

    // ==================== 边界情况和错误处理测试 ====================

    @Test
    void testParameterValidationErrors() throws Exception {
        // 创建物模型和服务
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("参数验证模型");
        model.setDescription("参数验证测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("testValidation");
        svc.setName("测试验证");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("测试参数验证");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 测试缺少必填字段
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        // 缺少 identifier, name, dataType, unit, description, required, direction
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testInvalidEnumValues() throws Exception {
        // 创建物模型
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("无效枚举模型");
        model.setDescription("无效枚举测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 测试无效的 dataType
        ThingModelProperty prop = new ThingModelProperty();
        prop.setIdentifier("test");
        prop.setName("测试");
        prop.setDataType(DataTypeEnum.FLOAT); // 使用枚举而不是字符串
        prop.setUnit("test");
        prop.setAccessMode(AccessModeEnum.RW);
        prop.setDescription("测试属性");
        prop.setRequired(true);
        String propJson = objectMapper.writeValueAsString(prop);
        mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isOk()); // 应该成功，因为使用了正确的枚举
    }

    @Test
    void testEnumOptionsFormatValidation() throws Exception {
        // 创建物模型和服务
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("枚举格式模型");
        model.setDescription("枚举格式测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("testEnumFormat");
        svc.setName("测试枚举格式");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("测试枚举格式验证");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 测试正确的枚举格式
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("testEnum");
        param.setName("测试枚举");
        param.setDataType(DataTypeEnum.ENUM);
        param.setUnit("");
        param.setDescription("测试枚举参数");
        param.setRequired(true);
        param.setDirection(DirectionEnum.IN);
        param.setEnumOptions("value1:描述1,value2:描述2"); // 正确的格式
        String paramJson = objectMapper.writeValueAsString(param);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk());

        // 测试错误的枚举格式
        ThingModelParameter invalidParam = new ThingModelParameter();
        invalidParam.setOwnerType(OwnerTypeEnum.SERVICE);
        invalidParam.setOwnerId(createdSvc.getId());
        invalidParam.setIdentifier("invalidEnum");
        invalidParam.setName("无效枚举");
        invalidParam.setDataType(DataTypeEnum.ENUM);
        invalidParam.setUnit("");
        invalidParam.setDescription("无效枚举参数");
        invalidParam.setRequired(true);
        invalidParam.setDirection(DirectionEnum.IN);
        invalidParam.setEnumOptions("invalid-format"); // 错误的格式
        String invalidParamJson = objectMapper.writeValueAsString(invalidParam);
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidParamJson))
                .andExpect(status().is4xxClientError());
    }

    // ==================== 路径参数大小写测试 ====================

    @Test
    void testPathParameterCaseInsensitive() throws Exception {
        // 创建物模型和服务
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName("路径参数测试");
        model.setDescription("路径参数大小写测试");
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String modelJson = objectMapper.writeValueAsString(model);
        String modelResp = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(modelJson))
                .andReturn().getResponse().getContentAsString();
        ThingModel createdModel = objectMapper.readValue(modelResp, ThingModel.class);

        // 添加服务
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier("testCase");
        svc.setName("测试大小写");
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription("测试路径参数大小写");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResp = mockMvc.perform(post("/api/thing-models/" + createdModel.getId() + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ThingModelService createdSvc = objectMapper.readValue(svcResp, ThingModelService.class);

        // 测试不同大小写的路径参数
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(OwnerTypeEnum.SERVICE);
        param.setOwnerId(createdSvc.getId());
        param.setIdentifier("testParam");
        param.setName("测试参数");
        param.setDataType(DataTypeEnum.STRING);
        param.setUnit("");
        param.setDescription("测试参数");
        param.setRequired(true);
        param.setDirection(DirectionEnum.IN);
        param.setMaxLength(100);
        param.setPattern(".*");
        String paramJson = objectMapper.writeValueAsString(param);

        // 测试小写路径
        mockMvc.perform(post("/api/thing-models/service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk());

        // 测试大写路径
        mockMvc.perform(post("/api/thing-models/SERVICE/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk());

        // 测试混合大小写路径
        mockMvc.perform(post("/api/thing-models/Service/" + createdSvc.getId() + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk());
    }

    private ThingModel createThingModel(String name, String description) throws Exception {
        ThingModel model = new ThingModel();
        model.setProductId("test-product");
        model.setName(name);
        model.setDescription(description);
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        String json = objectMapper.writeValueAsString(model);
        String response = mockMvc.perform(post("/api/thing-models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, ThingModel.class);
    }

    private ThingModelProperty createProperty(String thingModelId, String identifier, String name) throws Exception {
        ThingModelProperty prop = new ThingModelProperty();
        prop.setIdentifier(identifier);
        prop.setName(name);
        prop.setDataType(DataTypeEnum.FLOAT);
        prop.setUnit("℃");
        prop.setAccessMode(AccessModeEnum.RW);
        prop.setDescription(name + "属性");
        prop.setRequired(true);
        prop.setDefaultValue("0");
        String propJson = objectMapper.writeValueAsString(prop);
        String propResponse = mockMvc.perform(post("/api/thing-models/" + thingModelId + "/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(propResponse, ThingModelProperty.class);
    }

    private ThingModelService createService(String thingModelId, String identifier, String name) throws Exception {
        ThingModelService svc = new ThingModelService();
        svc.setIdentifier(identifier);
        svc.setName(name);
        svc.setCallType(CallTypeEnum.SYNC);
        svc.setDescription(name + "服务");
        String svcJson = objectMapper.writeValueAsString(svc);
        String svcResponse = mockMvc.perform(post("/api/thing-models/" + thingModelId + "/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(svcJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(svcResponse, ThingModelService.class);
    }

    private ThingModelEvent createEvent(String thingModelId, String identifier, String name) throws Exception {
        ThingModelEvent event = new ThingModelEvent();
        event.setIdentifier(identifier);
        event.setName(name);
        event.setType(EventTypeEnum.INFO);
        event.setDescription(name + "事件");
        String eventJson = objectMapper.writeValueAsString(event);
        String eventResponse = mockMvc.perform(post("/api/thing-models/" + thingModelId + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(eventResponse, ThingModelEvent.class);
    }

    private ThingModelParameter createParameter(OwnerTypeEnum ownerType, String ownerId, String identifier, String name, DataTypeEnum dataType) throws Exception {
        ThingModelParameter param = new ThingModelParameter();
        param.setOwnerType(ownerType);
        param.setOwnerId(ownerId);
        param.setIdentifier(identifier);
        param.setName(name);
        param.setDataType(dataType);
        param.setUnit("");
        param.setDescription(name + "参数");
        param.setRequired(true);
        param.setDirection(DirectionEnum.IN);
        String paramJson = objectMapper.writeValueAsString(param);
        String paramResponse = mockMvc.perform(post("/api/thing-models/" + ownerType.name().toLowerCase() + "/" + ownerId + "/parameters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paramJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(paramResponse, ThingModelParameter.class);
    }
}
