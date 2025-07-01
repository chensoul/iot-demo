package com.iot.demo.thingmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingModelServiceRepository extends JpaRepository<ThingModelService, String> {
    java.util.List<ThingModelService> findByThingModelId(String thingModelId);
}
