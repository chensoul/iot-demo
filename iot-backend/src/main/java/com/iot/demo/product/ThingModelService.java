package com.iot.demo.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThingModelService {
    private final ThingModelRepository thingModelRepository;

    public List<ThingModel> list(String productId) {
        return thingModelRepository.findByProductId(productId);
    }

    public Optional<ThingModel> getById(Long id) {
        return thingModelRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModel create(String productId, ThingModel thingModel) {
        thingModel.setProductId(productId);
        return thingModelRepository.save(thingModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModel update(String productId, Long id, ThingModel thingModel) {
        thingModel.setId(id);
        thingModel.setProductId(productId);
        return thingModelRepository.save(thingModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String productId, Long id) {
        thingModelRepository.deleteById(id);
    }
}
