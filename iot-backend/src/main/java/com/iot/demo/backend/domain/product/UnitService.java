package com.iot.demo.backend.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    public List<Unit> list() {
        return unitRepository.findAll();
    }
}
