package com.iot.demo.product;

import com.iot.demo.validation.NameFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Size(max = 10)
    @Column(nullable = false, unique = true, length = 10)
    private String productKey;

    @NotBlank(message = "名称不能为空")
    @Size(max = 40)
    @NameFormat
    @Column(nullable = false, unique = true, length = 40)
    private String name;

    @Enumerated(EnumType.STRING)
    private ProductNodeTypeEnum nodeType;
    @Enumerated(EnumType.STRING)
    private ProductAuthTypeEnum authType;
    @Enumerated(EnumType.STRING)
    private ProductNetTypeEnum netType;
    @Enumerated(EnumType.STRING)
    private ProductDataFormatEnum dataFormat;
    private String logo;
    private String description;
    private String categoryId;
    @Enumerated(EnumType.STRING)
    private ProductProtocolTypeEnum protocolType;
    @Enumerated(EnumType.STRING)
    private ProductStatusEnum status;

    @CreationTimestamp
    private LocalDateTime createTime;
    @UpdateTimestamp
    private LocalDateTime updateTime;

}
