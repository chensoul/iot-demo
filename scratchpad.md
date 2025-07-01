# 背景和动机

物联网（IoT）平台旨在连接、管理和监控大量异构设备，实现设备数据采集、远程控制、规则引擎、可视化等功能。该平台可服务于智能家居、工业自动化、智慧城市等多种场景，帮助企业提升运营效率和创新能力。

# 关键挑战和分析

- 设备多样性：需支持多种协议、数据格式和接入方式。
- 安全性：设备认证、数据加密、权限管理等。
- 可扩展性：平台需支持设备和用户的弹性扩展。
- 易用性：提供友好的管理界面和开放API。
- 数据处理：高效的数据采集、存储、分析与可视化。

# 高层任务拆分

1. 明确核心功能需求
    - 成功标准：输出详细的功能列表和优先级。
2. 设计系统架构（前后端、数据库、通信协议等）
    - 成功标准：产出系统架构图和技术选型文档。
3. 设备与产品建模（如产品、设备、物模型等）
    - 成功标准：实现产品/设备/物模型的增删改查API和数据库表。
4. 设备接入与通信（MQTT/HTTP等）
    - 成功标准：设备可通过标准协议接入并上报/下发数据。
5. 规则引擎与自动化
    - 成功标准：支持基于条件的自动化处理和告警。
6. 数据存储与可视化
    - 成功标准：设备数据可持久化、查询和可视化展示。
7. 用户与权限管理
    - 成功标准：支持多用户、角色和权限分配。
8. 前端管理界面
    - 成功标准：实现设备、产品、规则等核心功能的可视化管理。
9. 测试与部署
    - 成功标准：通过核心功能测试，具备一键部署能力。

# 系统架构设计与技术选型

## 架构总览

- 前端：管理控制台（Web），用于设备、产品、规则等可视化管理。
- 后端：RESTful API服务，负责业务逻辑、设备通信、数据处理。
- 设备接入层：支持MQTT/HTTP等协议，实现设备上下线、数据收发。
- 数据存储：关系型数据库（如MySQL/PostgreSQL）+时序数据库（如InfluxDB，可选）。
- 消息中间件：如EMQX/Mosquitto（MQTT Broker），实现高效设备消息转发。
- 规则与告警引擎：实现自动化处理和事件推送。
- 用户与权限管理：统一认证与权限分配。

## 技术选型建议

- 后端：Spring Boot 3（Java 17），Spring Data JPA，Spring Security
- 前端：React.js 或 Vue.js（可选Ant Design/Element UI等组件库）
- 数据库：MySQL，InfluxDB
- 消息中间件：EMQX
- 设备通信协议：MQTT（主推），HTTP（补充）
- 其他：Docker Compose（本地部署）、Swagger（API文档）、Nginx（反向代理）

## 架构草图（文字版）

```
[设备] <---MQTT/HTTP---> [设备接入层/消息中间件] <---REST API---> [后端服务(Spring Boot)] <---JDBC---> [MySQL/InfluxDB]
                                                                                 |
                                                                                 v
                                                                         [前端管理控制台]
```

> 说明：如需支持大规模设备或高可用，可后续引入微服务、分布式存储等。

# 详细核心功能需求

| 功能模块     | 功能描述                           | 优先级 |
|----------|--------------------------------|-----|
| 产品管理     | 定义产品类型、属性、协议、数据格式等             | 高   |
| 设备管理     | 设备注册、分组、状态监控、批量导入导出            | 高   |
| 物模型管理    | 支持属性、事件、服务等物模型定义和版本管理          | 高   |
| 设备接入     | 支持MQTT/HTTP等协议，设备上下线、数据上报、命令下发 | 高   |
| 数据存储     | 设备数据、事件、日志等持久化存储               | 高   |
| 数据查询与可视化 | 历史数据查询、实时数据展示、图表可视化            | 中   |
| 规则引擎     | 条件触发、联动控制、告警推送                 | 中   |
| 用户与权限管理  | 多用户、角色、权限分配                    | 中   |
| API开放    | 提供RESTful/OpenAPI等接口，便于第三方集成   | 中   |
| 前端管理界面   | 设备、产品、规则等可视化管理界面               | 高   |
| 日志与审计    | 操作日志、设备日志、系统审计                 | 低   |
| 运维与监控    | 平台运行状态监控、告警、健康检查               | 低   |

> 说明：如有特定业务场景或功能需求，请补充。

# 设备与产品建模方案

## 1. 产品(Product)与产品分类(ProductCategory)

- 产品(Product)：
    - 字段：id, productKey, name, nodeType, authType, netType, dataFormat, logo, description, categoryId, protocolType,
      status, templateId, createTime, updateTime
    - 说明：每个产品归属于一个产品分类，可选用物模型模板。
- 产品分类(ProductCategory)：
    - 字段：id, name, description, icon, sortOrder, parentId, createTime, updateTime
    - 说明：支持多级分类，便于产品归类和模板复用。

## 2. 设备(Device)

- 字段建议：id, name, productId, activeStatus, onlineStatus, registerTime, lastOnlineTime, ...
- 说明：设备实例化自某个产品，继承其物模型。

## 3. 物模型(ThingModel)及模板(ThingModelTemplate)

- 物模型(ThingModel)：
    - 字段：id, productId, name, description,templateData, createTime, updateTime
    - 说明：定义产品的属性、事件、服务。
- 物模型模板(ThingModelTemplate)：
    - 字段：id, name, description, categoryId, status, version, templateData(JSON),
      usageCount, createTime, updateTime, publishTime
    - 说明：可预设常用设备类型的标准物模型，便于产品快速建模和复用。

> 说明：如需支持多租户、设备分组、扩展字段等，可在后续细化。

# 项目状态看板

- [x] 明确核心功能需求
- [x] 设计系统架构
- [x] 设备与产品建模
- [ ] 设备接入与通信
- [ ] 规则引擎与自动化
- [ ] 数据存储与可视化
- [ ] 用户与权限管理
- [ ] 前端管理界面
- [ ] 测试与部署

# 执行者反馈或请求帮助

## 代码规范与本次执行说明

- Controller 只负责接收参数，所有业务相关的属性赋值、校验、转换等都应放到 Service 层。
- Service 类的新增、修改、删除等方法，添加 @Transactional(rollbackFor = Exception.class) 注解。
- 使用注解进行校验
- 所有代码中不再使用 @Autowired，全部改为 lombok 的构造方法注入（@RequiredArgsConstructor）。
- 所有类均通过 import 导入类名，不使用全限定名。
- 所有模型的 Service 和 Controller 方法命名需保持一致，统一规范如下：
    - 新增：create
    - 查询单个：getById
    - 查询列表：list
    - 更新：update
    - 删除：delete 或 deleteById
    - 其他业务动作：动词+业务名（如 publishTemplate、deprecateTemplate）

（等待任务执行后补充）

# 经验教训

（项目推进过程中补充）

> 设备与产品建模相关开发、接口、单元测试、数据库适配等全部完成，已通过验收。后续如有新需求可在此基础上扩展。
