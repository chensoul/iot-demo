# 阿里巴巴物联网平台模型设计文档

## 1. 物模型核心概念

- **产品(Product)**：同类设备的抽象集合，定义功能、协议、数据格式等。
- **设备(Device)**：产品的具体实例，拥有唯一身份标识。
- **产品分类(ProductCategory)**：多级分类体系，便于产品归类和模板复用。
- **物模型(ThingModel)**：描述设备功能的标准化模型，包括属性、事件、服务。
- **属性(Property)**：设备的状态量，可读/写/上报。
- **事件(Event)**：设备主动上报的重要信息，如告警、状态变化。
- **服务(Service)**：平台下发的命令或设备提供的操作能力。
- **物模型模板(ThingModelTemplate)**：可复用的物模型定义，便于批量建模。

## 2. 主要对象数据结构（字段建议）

### 产品(Product)

- id, productKey, name, nodeType, authType, netType, dataFormat, logo, description, categoryId, protocolType, status,
  templateId, createTime, updateTime
- **name（产品名称）校验规则：**
    - 允许字符：中文、英文字母、日文、数字、下划线（_）、短划线（-）、at（@）、英文圆括号（()）
    - 长度限制：4~30个字符（一个中文或日文算2个字符）
    - 建议正则表达式：`^[\u4e00-\u9fa5\u3040-\u30ffA-Za-z0-9_\-@()]{4,30}# 阿里巴巴物联网平台模型设计文档

## 1. 物模型核心概念

- **产品(Product)**：同类设备的抽象集合，定义功能、协议、数据格式等。
- **设备(Device)**：产品的具体实例，拥有唯一身份标识。
- **产品分类(ProductCategory)**：多级分类体系，便于产品归类和模板复用。
- **物模型(ThingModel)**：描述设备功能的标准化模型，包括属性、事件、服务。
- **属性(Property)**：设备的状态量，可读/写/上报。
- **事件(Event)**：设备主动上报的重要信息，如告警、状态变化。
- **服务(Service)**：平台下发的命令或设备提供的操作能力。
- **物模型模板(ThingModelTemplate)**：可复用的物模型定义，便于批量建模。

## 2. 主要对象数据结构（字段建议）

（需结合实际多字节长度校验）

- 实现建议：先用正则校验字符合法性，再用自定义方法统计长度（中文/日文2，其他1）
- **品类选择机制：**
    - 支持标准品类（从产品分类中选择）和自定义品类（用户新建），`categoryId` 可指向任意分类。
    - 支持产品创建后自主编辑物模型（不强制绑定模板，可自定义属性、事件、服务等）。

- **产品(Product)**：同类设备的抽象集合，定义功能、协议、数据格式等。
- **设备(Device)**：产品的具体实例，拥有唯一身份标识。
- **产品分类(ProductCategory)**：多级分类体系，便于产品归类和模板复用。
- **物模型(ThingModel)**：描述设备功能的标准化模型，包括属性、事件、服务。
- **属性(Property)**：设备的状态量，可读/写/上报。
- **事件(Event)**：设备主动上报的重要信息，如告警、状态变化。
- **服务(Service)**：平台下发的命令或设备提供的操作能力。
- **物模型模板(ThingModelTemplate)**：可复用的物模型定义，便于批量建模。

## 2. 主要对象数据结构（字段建议）

（需结合实际多字节长度校验）

- 实现建议：先用正则校验字符合法性，再用自定义方法统计长度（中文/日文2，其他1）

### 产品分类(ProductCategory)

- id, name, description, icon, sortOrder, parentId, createTime, updateTime

### 设备(Device)

- id, name, productId, activeStatus, onlineStatus, registerTime, lastOnlineTime, ...

### 物模型(ThingModel)

- id, productId, name, description, createTime, updateTime

### 物模型属性(Property)

- id, thingModelId, identifier, name, dataType, unit, accessMode, description, required, defaultValue

### 物模型事件(Event)

- id, thingModelId, identifier, name, type, description

### 物模型服务(Service)

- id, thingModelId, identifier, name, callType, description

### 物模型模板(ThingModelTemplate)

- id, templateKey, name, description, categoryId, status, version, author, tags, templateData(JSON), usageCount,
  createTime, updateTime, publishTime

### 物模型参数(ThingModelParameter)

- id, ownerType, ownerId, identifier, name, dataType, unit, description, required, direction, minValue, maxValue, step,
  maxLength, pattern, enumOptions, parentId
- 说明：
    - ownerType/ownerId：标识参数归属（属性、事件、服务等）。
    - 支持嵌套结构体、数组、枚举等复杂参数类型。
    - parentId 支持参数树形结构（如结构体/数组的子字段）。

## 3. 分类体系与模板机制

- 支持多级产品分类（如：家电 > 空调、传感器 > 温湿度、网关等），便于行业归类和模板复用。
- 物模型模板可预设常用设备类型（如温湿度传感器、智能插座等），支持版本管理和分类筛选。
- 产品创建时可选择分类和物模型模板，提升建模效率。

## 4. 设计原则与最佳实践

- 结构化、标准化：属性、事件、服务均有唯一标识、类型、描述等字段。
- 可扩展性：支持自定义扩展字段、嵌套结构体、枚举等。
- 复用性：模板机制支持行业标准和企业自定义模板。
- 多级分类：便于大规模产品管理和模板推荐。
- 版本管理：模板和物模型支持版本号，便于升级和兼容。

## 5. 典型物模型模板示例

### 温湿度传感器模板

```json
{
  "properties": [
    {
      "identifier": "temperature",
      "name": "温度",
      "dataType": "float",
      "unit": "℃",
      "accessMode": "r",
      "description": "当前温度"
    },
    {
      "identifier": "humidity",
      "name": "湿度",
      "dataType": "float",
      "unit": "%",
      "accessMode": "r",
      "description": "当前湿度"
    },
    {
      "identifier": "battery",
      "name": "电池电量",
      "dataType": "int",
      "unit": "%",
      "accessMode": "r",
      "description": "剩余电量"
    },
    {
      "identifier": "status",
      "name": "工作状态",
      "dataType": "enum",
      "accessMode": "r",
      "description": "设备状态",
      "enumOptions": "0:正常,1:故障,2:维护"
    },
    {
      "identifier": "lastUpdate",
      "name": "最后更新时间",
      "dataType": "date",
      "accessMode": "r",
      "description": "数据更新时间"
    },
    {
      "identifier": "firmwareVersion",
      "name": "固件版本",
      "dataType": "string",
      "accessMode": "r",
      "description": "当前固件版本"
    },
    {
      "identifier": "isOnline",
      "name": "在线状态",
      "dataType": "bool",
      "accessMode": "r",
      "description": "设备是否在线"
    },
    {
      "identifier": "rawData",
      "name": "原始数据",
      "dataType": "binary",
      "accessMode": "r",
      "description": "原始二进制数据"
    },
    {
      "identifier": "history",
      "name": "历史记录",
      "dataType": "array",
      "accessMode": "r",
      "description": "历史温湿度数组"
    },
    {
      "identifier": "location",
      "name": "地理位置",
      "dataType": "struct",
      "accessMode": "r",
      "description": "经纬度结构体",
      "structSpecs": [
        {
          "identifier": "lat",
          "name": "纬度",
          "dataType": "double"
        },
        {
          "identifier": "lng",
          "name": "经度",
          "dataType": "double"
        }
      ]
    }
  ],
  "events": [
    {
      "identifier": "overheat",
      "name": "过温告警",
      "type": "alarm",
      "description": "温度超限",
      "parameters": [
        {
          "identifier": "currentTemp",
          "name": "当前温度",
          "dataType": "float",
          "direction": "output",
          "description": "触发时温度"
        },
        {
          "identifier": "threshold",
          "name": "阈值",
          "dataType": "float",
          "direction": "output",
          "description": "告警阈值"
        }
      ]
    }
  ],
  "services": [
    {
      "identifier": "reset",
      "name": "重置设备",
      "callType": "async",
      "description": "远程重启设备",
      "inputParameters": [
        {
          "identifier": "force",
          "name": "强制重启",
          "dataType": "bool",
          "direction": "input",
          "description": "是否强制"
        }
      ],
      "outputParameters": [
        {
          "identifier": "result",
          "name": "结果",
          "dataType": "enum",
          "enumOptions": "0:成功,1:失败",
          "direction": "output",
          "description": "执行结果"
        }
      ]
    }
  ]
}
```

---
> 本文档可作为平台建模和开发的参考，如需扩展请补充具体业务场景和字段。

## 6. 参考链接

- [阿里云物联网平台 物模型官方文档](https://help.aliyun.com/zh/iot/developer-reference/thing-model-introduction)
- [物模型详细规范（属性、事件、服务、数据类型等）](https://help.aliyun.com/zh/iot/developer-reference/thing-model-specification)
- [物模型开发最佳实践](https://help.aliyun.com/zh/iot/developer-reference/thing-model-best-practices)
