package com.iot.demo.thingmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThingModelServiceLayer {
    private final ThingModelRepository thingModelRepository;
    private final ThingModelPropertyRepository propertyRepository;
    private final ThingModelServiceRepository serviceRepository;
    private final ThingModelEventRepository eventRepository;
    private final ThingModelParameterRepository parameterRepository;

    // ThingModel CRUD
    @Transactional(rollbackFor = Exception.class)
    public ThingModel create(ThingModel thingModel) {
        return thingModelRepository.save(thingModel);
    }

    public List<ThingModel> list() {
        return thingModelRepository.findAll();
    }

    public Optional<ThingModel> getById(String id) {
        return thingModelRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ThingModel update(String id, ThingModel update) {
        update.setId(id);
        return thingModelRepository.save(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        thingModelRepository.deleteById(id);
    }

    // Property CRUD
    @Transactional(rollbackFor = Exception.class)
    public ThingModelProperty createProperty(String thingModelId, ThingModelProperty property) {
        property.setThingModelId(thingModelId);
        return propertyRepository.save(property);
    }

    public List<ThingModelProperty> listProperty(String thingModelId) {
        return propertyRepository.findByThingModelId(thingModelId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProperty(String id) {
        propertyRepository.deleteById(id);
    }

    // Service CRUD
    @Transactional(rollbackFor = Exception.class)
    public ThingModelService createService(String thingModelId, ThingModelService serviceEntity) {
        serviceEntity.setThingModelId(thingModelId);
        return serviceRepository.save(serviceEntity);
    }

    public List<ThingModelService> listService(String thingModelId) {
        return serviceRepository.findByThingModelId(thingModelId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteService(String id) {
        serviceRepository.deleteById(id);
    }

    // Event CRUD
    @Transactional(rollbackFor = Exception.class)
    public ThingModelEvent createEvent(String thingModelId, ThingModelEvent event) {
        event.setThingModelId(thingModelId);
        return eventRepository.save(event);
    }

    public List<ThingModelEvent> listEvent(String thingModelId) {
        return eventRepository.findByThingModelId(thingModelId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }

    // Parameter CRUD
    @Transactional(rollbackFor = Exception.class)
    public ThingModelParameter createParameter(OwnerTypeEnum ownerType, String ownerId, ThingModelParameter parameter) {
        parameter.setOwnerType(ownerType);
        parameter.setOwnerId(ownerId);
        return parameterRepository.save(parameter);
    }

    public List<ThingModelParameter> listParameter(OwnerTypeEnum ownerType, String ownerId) {
        return parameterRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteParameter(String id) {
        parameterRepository.deleteById(id);
    }
}
