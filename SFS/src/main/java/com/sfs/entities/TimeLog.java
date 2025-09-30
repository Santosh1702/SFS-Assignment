package com.sfs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "device_id")
    @JsonIgnore
    private Device device;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    public TimeLog() {
    }

    public TimeLog(Employee employee, Device device, Instant startTime) {
        this.employee = employee;
        this.device = device;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
