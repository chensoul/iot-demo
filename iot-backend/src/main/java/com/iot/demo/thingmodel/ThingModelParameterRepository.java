package com.iot.demo.thingmodel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingModelParameterRepository extends JpaRepository<ThingModelParameter, String> {
    java.util.List<ThingModelParameter> findByOwnerTypeAndOwnerId(OwnerTypeEnum ownerType, String ownerId);
}
