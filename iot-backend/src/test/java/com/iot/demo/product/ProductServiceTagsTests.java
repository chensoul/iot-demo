package com.iot.demo.product;

import com.iot.demo.backend.domain.product.Product;
import com.iot.demo.backend.domain.product.ProductRepository;
import com.iot.demo.backend.domain.product.ProductService;
import com.iot.demo.backend.domain.product.ProductStatusEnum;
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
class ProductServiceTagsTests {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    void testProductTagsCRUDAndQuery() {
        // 新建产品，带唯一前缀
        String unique = "test";
        final Product product = new Product();
        product.setName(unique);
        product.setProductKey(unique);
        product.setProductSecret("secret");
        product.setStatus(ProductStatusEnum.DEV);
        productRepository.save(product);

        // 批量添加标签
        Map<String, String> tags = new HashMap<>();
        tags.put("a", "1");
        tags.put("b", "2");
        productService.updateTags(product.getId(), tags);

        // 查询标签
        Map<String, String> got = productService.getTags(product.getId());
        Assertions.assertEquals("1", got.get("a"));
        Assertions.assertEquals("2", got.get("b"));

        // 按标签批量查询
        Map<String, String> queryTags = new HashMap<>();
        queryTags.put("a", "1");
        queryTags.put("b", "2");
        List<Product> found = productService.findByTags(queryTags);
        Assertions.assertTrue(found.stream().anyMatch(p -> p.getId().equals(product.getId())));

        // 更新标签
        tags.put("a", "3");
        productService.updateTags(product.getId(), tags);
        got = productService.getTags(product.getId());
        Assertions.assertEquals("3", got.get("a"));
        Assertions.assertEquals("2", got.get("b"));
    }
} 