package com.sfs.service;

import com.sfs.entities.Plant;
import java.util.List;
import java.util.Optional;

public interface PlantService {

	List<Plant> findAllPlants();

    Optional<Plant> findByPlantKey(String plantKey);
}