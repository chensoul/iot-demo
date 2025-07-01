package com.iot.demo.thingmodel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ThingModelService {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String thingModelId;
    @NotBlank(message = "服务 identifier 不能为空")
    private String identifier;
    private String name;
    @Enumerated(EnumType.STRING)
    private CallTypeEnum callType;
    private String description;

}
