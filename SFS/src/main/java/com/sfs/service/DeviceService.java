package com.sfs.service;

import com.sfs.entities.Device;
import com.sfs.entities.Plant;
import com.sfs.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import reactor.core.publisher.Mono;

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
    
    public Mono<Device> getDeviceById(Long deviceId) {
        return Mono.fromSupplier(() -> deviceRepository.findByDeviceIdWithGroups(deviceId))
                .flatMap(Mono::justOrEmpty)
                .switchIfEmpty(Mono.error(() -> new EntityNotFoundException("Device not found.")));
    }

    public List<Device> findByPlantKey(String plantKey) {
        return deviceRepository.findByPlantKey(plantKey);
    }
}