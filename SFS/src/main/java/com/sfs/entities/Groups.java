package com.sfs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "Groups")
@Table(name = "groups_table") 
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String plantKey;

    @ManyToMany
    @JoinTable(
        name = "device_group", 
        joinColumns = @JoinColumn(name = "group_id"), 
        inverseJoinColumns = @JoinColumn(name = "device_id") 
    )
    @JsonIgnore
    private Set<Device> devices = new HashSet<>(); 

    @ManyToMany(mappedBy = "groups")
    @JsonIgnore
    private Set<Employee> employees = new HashSet<>();

    public Groups() {
    }

    public Groups(String name, String plantKey) {
        this.name = name;
        this.plantKey = plantKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlantKey() {
        return plantKey;
    }

    public void setPlantKey(String plantKey) {
        this.plantKey = plantKey;
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public void setDevices(Set<Device> devices) {
        this.devices = devices;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }
}