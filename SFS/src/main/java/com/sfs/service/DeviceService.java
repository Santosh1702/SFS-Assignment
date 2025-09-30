package com.sfs.service;

import com.sfs.entities.Device;
import com.sfs.entities.Plant;
import com.sfs.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final PlantService plantService;

    public DeviceService(DeviceRepository deviceRepository, PlantService plantService) {
        this.deviceRepository = deviceRepository;
        this.plantService = plantService;
    }

    public Device save(Device device) {
        Optional<Plant> plant = plantService.findByPlantKey(device.getPlantKey());
        if (plant.isEmpty()) {
            throw new IllegalArgumentException("Invalid Plant Key: " + device.getPlantKey());
        }

        if (device.getDeviceId() != null && !deviceRepository.existsById(device.getDeviceId())) {
            // Optional: If deviceId is provided, it must be an update or a new insert with a manually set ID
        }

        return deviceRepository.save(device);
    }

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }
    
    /**
     * CORRECTION: Synchronous replacement for the removed reactive method.
     * Finds a Device by its ID (primary key or external ID, depending on the Entity structure) 
     * and fetches its associated Groups in the same transaction.
     */
    public Optional<Device> findByDeviceIdWithGroups(Long deviceId) {
        return deviceRepository.findByDeviceIdWithGroups(deviceId);
    }

    public List<Device> findByPlantKey(String plantKey) {
        return deviceRepository.findByPlantKey(plantKey);
    }
}