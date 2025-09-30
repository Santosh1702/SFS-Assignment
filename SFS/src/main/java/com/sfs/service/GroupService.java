package com.sfs.service;

import com.sfs.entities.Groups;
import com.sfs.entities.Device;
import com.sfs.entities.Plant;
import com.sfs.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final PlantService plantService;
    private final DeviceService deviceService;

    public GroupService(GroupRepository groupRepository, PlantService plantService, DeviceService deviceService) {
        this.groupRepository = groupRepository;
        this.plantService = plantService;
        this.deviceService = deviceService;
    }

    public Groups save(Groups group) {
        Optional<Plant> plant = plantService.findByPlantKey(group.getPlantKey());
        if (plant.isEmpty()) {
            throw new IllegalArgumentException("Invalid Plant Key: " + group.getPlantKey());
        }

        groupRepository.findByName(group.getName()).ifPresent(g -> {
            if (!g.getId().equals(group.getId())) {
                throw new IllegalArgumentException("Group name must be unique: " + group.getName());
            }
        });

        return groupRepository.save(group);
    }

    public Optional<Groups> findById(Long id) {
        return groupRepository.findById(id);
    }


    public Groups assignToDevices(Long groupId, Set<Long> deviceIds) {
        Groups group = groupRepository.findById(groupId)
            .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupId));

        Set<Device> newDevices = new HashSet<>();
        for (Long deviceId : deviceIds) {
            Device device = deviceService.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("Device not found with ID: " + deviceId));

            if (!group.getPlantKey().equals(device.getPlantKey())) {
                throw new IllegalArgumentException(
                    "Assignment failure: Group '" + group.getName() + "' is at Plant " + group.getPlantKey() +
                    ", but Device '" + deviceId + "' is at Plant " + device.getPlantKey() + ". They must match."
                );
            }
            newDevices.add(device);
        }

        group.setDevices(newDevices);
        return groupRepository.save(group);
    }
}