package com.iot.demo.thingmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingModelPropertyRepository extends JpaRepository<ThingModelProperty, String> {
    java.util.List<ThingModelProperty> findByThingModelId(String thingModelId);
}
