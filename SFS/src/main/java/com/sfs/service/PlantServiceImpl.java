package com.sfs.service;

import com.sfs.config.PlantConfig;
import com.sfs.entities.Plant;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service 
public class PlantServiceImpl implements PlantService {

    private final PlantConfig plantConfig;

    public PlantServiceImpl(PlantConfig plantConfig) {
        this.plantConfig = plantConfig;
    }

    @Override
    public List<Plant> findAllPlants() {
        return plantConfig.getList();
    }

    @Override
    public Optional<Plant> findByPlantKey(String plantKey) {
        return plantConfig.getList().stream()
                .filter(plant -> plant.getPlantKey().equals(plantKey))
                .findFirst();
    }
}