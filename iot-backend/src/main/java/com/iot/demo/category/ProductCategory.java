package com.iot.demo.category;

import com.iot.demo.validation.NameFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "名称不能为空")
    @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    @NameFormat
    private String name;

    @Column(length = 256)
    private String description;

    private String icon;

    private Integer sortOrder;

    // 父级分类ID
    private String parentId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
