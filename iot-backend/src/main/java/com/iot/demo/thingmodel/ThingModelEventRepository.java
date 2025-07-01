package com.iot.demo.thingmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingModelEventRepository extends JpaRepository<ThingModelEvent, String> {
    java.util.List<ThingModelEvent> findByThingModelId(String thingModelId);
}
