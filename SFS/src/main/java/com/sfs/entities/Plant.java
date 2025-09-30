package com.sfs.entities;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Plant {

    @NotBlank
    @Size(max = 50)
    private String plantKey;

    @NotBlank
    @Size(max = 100)
    private String location;

    public Plant() {
    }

    public Plant(String plantKey, String location) {
        this.plantKey = plantKey;
        this.location = location;
    }

    public String getPlantKey() {
        return plantKey;
    }

    public void setPlantKey(String plantKey) {
        this.plantKey = plantKey;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}