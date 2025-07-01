package com.iot.demo.product;

import com.iot.demo.category.ProductCategoryService;
import com.iot.demo.category.ThingModelTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ThingModelTemplateRepository templateRepository;
    private final ProductCategoryService productCategoryService;

    public List<Product> listByCategoryId(Long categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId);
        }
        return productRepository.findAll();
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product create(Product product) {
        product.setProductKey(generateRandomKey(10));
        product.setProductSecret(generateRandomKey(20));
        product.setStatus(ProductStatusEnum.DEV);
        Product saved = productRepository.save(product);

        if (product.getCategoryId() != null) {
            if (productCategoryService.getById(product.getCategoryId()).isEmpty()) {
                throw new IllegalArgumentException("分类ID不存在");
            }
            templateRepository.findFirstByCategoryIdAndStatusOrderByVersionDesc(product.getCategoryId(), TemplateStatusEnum.RELEASE)
                    .ifPresent(template -> {
                    });
        }
        return saved;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product update(String id, Product.ProductUpdate productUpdate) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setName(productUpdate.getName());
        product.setDescription(productUpdate.getDescription());
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product publish(String id) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setStatus(ProductStatusEnum.RELEASE);
        return productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public Product revoke(String id) {
        Product product = getById(id).orElseThrow(() -> new IllegalArgumentException("产品不存在"));
        product.setStatus(ProductStatusEnum.DEV);
        return productRepository.save(product);
    }

    private String generateRandomKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
