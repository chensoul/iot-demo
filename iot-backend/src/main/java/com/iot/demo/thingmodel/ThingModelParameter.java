package com.iot.demo.thingmodel;

import com.iot.demo.validation.EnumOptionsFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ThingModelParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "ownerType 不能为空")
    @Enumerated(EnumType.STRING)
    private OwnerTypeEnum ownerType;
    @NotBlank(message = "ownerId 不能为空")
    private String ownerId;
    @NotBlank(message = "参数 identifier 不能为空")
    private String identifier;
    @NotBlank(message = "name 不能为空")
    private String name;
    @NotNull(message = "dataType 不能为空")
    @Enumerated(EnumType.STRING)
    private DataTypeEnum dataType;
    private String unit;
    private String description;
    @NotNull(message = "required 不能为空")
    private Boolean required;
    @NotNull(message = "direction 不能为空")
    @Enumerated(EnumType.STRING)
    private DirectionEnum direction;

    // --- 结构化字段 ---
    private String minValue;    // 数值型最小值
    private String maxValue;    // 数值型最大值
    private String step;        // 数值型步长
    @Min(value = 1, message = "maxLength 必须大于0")
    private Integer maxLength;  // 字符串最大长度
    private String pattern;     // 正则表达式
    @EnumOptionsFormat
    private String enumOptions; // 枚举值，支持 value:desc,value:desc 或 JSON

    /**
     * 支持嵌套结构体/数组参数：
     * parentId=null 表示顶层参数，parentId=父参数id 表示为结构体/数组的子字段。
     * 递归查询 parentId 可还原树形结构。
     */
    private String parentId; // 父参数ID，支持嵌套结构体
}
