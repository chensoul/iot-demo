package com.iot.demo.backend.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/units")
public class UnitController {
    private final UnitService unitService;

    @GetMapping
    public List<Unit> list() {
        return unitService.list();
    }
}
