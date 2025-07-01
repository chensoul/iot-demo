package com.iot.demo.backend.domain.category;

import com.iot.demo.backend.validation.NameFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "名称不能为空")
    @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    @NameFormat
    private String name;

    private String code;

    // 父级分类ID
    private Long parentId;

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;

    @Version
    private LocalDateTime version;
}
