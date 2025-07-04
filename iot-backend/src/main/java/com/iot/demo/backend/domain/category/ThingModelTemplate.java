package com.iot.demo.backend.domain.category;

import com.iot.demo.backend.domain.product.TemplateStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "thing_model_template")
public class ThingModelTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    // 分类ID，指向产品分类表
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    private TemplateStatusEnum status;

    @Column(columnDefinition = "TEXT")
    private String templateData;

    private Integer usageCount;

    @CreationTimestamp
    private LocalDateTime createTime;
    @UpdateTimestamp
    private LocalDateTime updateTime;

    private LocalDateTime publishTime;

    @Version
    private LocalDateTime version;

    @Data
    public class ThingModelTemplateUpdate {
        @NotBlank(message = "模版名称不能为空")
        private String name;

        private String description;

        @NotBlank(message = "模版 TSL 不能为空")
        private String templateData;
    }
}
