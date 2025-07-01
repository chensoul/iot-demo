package com.iot.demo.backend.domain.device;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceGroupService {
    private final DeviceGroupRepository groupRepository;
    private final DeviceGroupRelationRepository relationRepository;
    private final DeviceRepository deviceRepository;

    public List<DeviceGroup> list() {
        return groupRepository.findAll();
    }

    public Optional<DeviceGroup> getById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceGroup create(DeviceGroup group) {
        return groupRepository.save(group);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceGroup update(Long id, DeviceGroup update) {
        DeviceGroup group = groupRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("分组不存在"));
        group.setName(update.getName());
        group.setParentId(update.getParentId());
        group.setDescription(update.getDescription());
        return groupRepository.save(group);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        groupRepository.deleteById(id);
    }

    public List<DeviceGroup> getChildren(Long parentId) {
        return groupRepository.findByParentId(parentId);
    }

    public List<DeviceGroupTreeDTO> getGroupTree() {
        List<DeviceGroup> all = groupRepository.findAll();
        return buildTreeDTO(null, all);
    }

    private List<DeviceGroupTreeDTO> buildTreeDTO(Long parentId, List<DeviceGroup> all) {
        List<DeviceGroupTreeDTO> children = new ArrayList<>();
        for (DeviceGroup group : all) {
            if ((parentId == null && group.getParentId() == null) || (parentId != null && parentId.equals(group.getParentId()))) {
                DeviceGroupTreeDTO dto = new DeviceGroupTreeDTO();
                dto.setId(group.getId());
                dto.setName(group.getName());
                dto.setParentId(group.getParentId());
                dto.setDescription(group.getDescription());
                dto.setChildren(buildTreeDTO(group.getId(), all));
                children.add(dto);
            }
        }
        return children;
    }

    public List<DeviceGroupTreeWithDevicesDTO> getGroupTreeWithDevices() {
        List<DeviceGroup> allGroups = groupRepository.findAll();
        List<Device> allDevices = deviceRepository.findAll();
        List<DeviceGroupRelation> allRelations = relationRepository.findAll();
        return buildTreeWithDevicesDTO(null, allGroups, allDevices, allRelations);
    }

    private List<DeviceGroupTreeWithDevicesDTO> buildTreeWithDevicesDTO(Long parentId, List<DeviceGroup> allGroups, List<Device> allDevices, List<DeviceGroupRelation> allRelations) {
        List<DeviceGroupTreeWithDevicesDTO> children = new ArrayList<>();
        for (DeviceGroup group : allGroups) {
            if ((parentId == null && group.getParentId() == null) || (parentId != null && parentId.equals(group.getParentId()))) {
                DeviceGroupTreeWithDevicesDTO dto = new DeviceGroupTreeWithDevicesDTO();
                dto.setId(group.getId());
                dto.setName(group.getName());
                dto.setParentId(group.getParentId());
                dto.setDescription(group.getDescription());
                dto.setChildren(buildTreeWithDevicesDTO(group.getId(), allGroups, allDevices, allRelations));
                // 关联设备（多对多）
                List<Device> devices = new ArrayList<>();
                for (DeviceGroupRelation rel : allRelations) {
                    if (rel.getGroupId().equals(group.getId())) {
                        for (Device device : allDevices) {
                            if (device.getId().equals(rel.getDeviceId())) {
                                devices.add(device);
                            }
                        }
                    }
                }
                dto.setDevices(devices);
                children.add(dto);
            }
        }
        return children;
    }
} 