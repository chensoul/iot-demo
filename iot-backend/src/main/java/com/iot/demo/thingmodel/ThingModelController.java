package com.iot.demo.thingmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/thing-models")
public class ThingModelController {
    private final ThingModelServiceLayer service;

    // ThingModel
    @PostMapping
    public ThingModel create(@RequestBody ThingModel thingModel) {
        return service.create(thingModel);
    }

    @GetMapping
    public List<ThingModel> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ThingModel getById(@PathVariable String id) {
        return service.getById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public ThingModel update(@PathVariable String id, @RequestBody ThingModel thingModel) {
        return service.update(id, thingModel);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    // Property
    @PostMapping("/{thingModelId}/properties")
    public ThingModelProperty createProperty(@PathVariable String thingModelId, @RequestBody @jakarta.validation.Valid ThingModelProperty property) {
        return service.createProperty(thingModelId, property);
    }

    @GetMapping("/{thingModelId}/properties")
    public List<ThingModelProperty> listProperty(@PathVariable String thingModelId) {
        return service.listProperty(thingModelId);
    }

    @DeleteMapping("/properties/{id}")
    public void deleteProperty(@PathVariable String id) {
        service.deleteProperty(id);
    }

    // Service
    @PostMapping("/{thingModelId}/services")
    public ThingModelService createService(@PathVariable String thingModelId, @RequestBody @jakarta.validation.Valid ThingModelService svc) {
        return service.createService(thingModelId, svc);
    }

    @GetMapping("/{thingModelId}/services")
    public List<ThingModelService> listService(@PathVariable String thingModelId) {
        return service.listService(thingModelId);
    }

    @DeleteMapping("/services/{id}")
    public void deleteService(@PathVariable String id) {
        service.deleteService(id);
    }

    // Event
    @PostMapping("/{thingModelId}/events")
    public ThingModelEvent createEvent(@PathVariable String thingModelId, @RequestBody @jakarta.validation.Valid ThingModelEvent event) {
        return service.createEvent(thingModelId, event);
    }

    @GetMapping("/{thingModelId}/events")
    public List<ThingModelEvent> listEvent(@PathVariable String thingModelId) {
        return service.listEvent(thingModelId);
    }

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable String id) {
        service.deleteEvent(id);
    }

    // Parameter
    @PostMapping("/{ownerType}/{ownerId}/parameters")
    public ThingModelParameter createParameter(@PathVariable OwnerTypeEnum ownerType, @PathVariable String ownerId, @RequestBody @jakarta.validation.Valid ThingModelParameter param) {
        return service.createParameter(ownerType, ownerId, param);
    }

    @GetMapping("/{ownerType}/{ownerId}/parameters")
    public List<ThingModelParameter> listParameter(@PathVariable OwnerTypeEnum ownerType, @PathVariable String ownerId) {
        return service.listParameter(ownerType, ownerId);
    }

    @DeleteMapping("/parameters/{id}")
    public void deleteParameter(@PathVariable String id) {
        service.deleteParameter(id);
    }
}
