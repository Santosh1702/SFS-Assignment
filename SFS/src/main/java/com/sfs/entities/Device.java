package com.sfs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Device {

    @Id
    @NotNull
    private Long deviceId;

    @NotBlank
    @Size(max = 50)
    private String type;

    @NotNull
    @PositiveOrZero
    private Integer requiredOperators;

    @NotBlank
    @Size(max = 50)
    private String plantKey;

    @ManyToMany(mappedBy = "devices") 
    @JsonIgnore
    private Set<Groups> groups = new HashSet<>(); 


    public Device() {
    }

    public Device(Long deviceId, String type, Integer requiredOperators, String plantKey) {
        this.deviceId = deviceId;
        this.type = type;
        this.requiredOperators = requiredOperators;
        this.plantKey = plantKey;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getRequiredOperators() {
        return requiredOperators;
    }

    public void setRequiredOperators(Integer requiredOperators) {
        this.requiredOperators = requiredOperators;
    }

    public String getPlantKey() {
        return plantKey;
    }

    public void setPlantKey(String plantKey) {
        this.plantKey = plantKey;
    }

    public Set<Groups> getGroups() {
        return groups;
    }

    public void setGroups(Set<Groups> groups) {
        this.groups = groups;
    }
}