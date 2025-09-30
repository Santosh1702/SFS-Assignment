package com.sfs.service;

import com.sfs.entities.Device;
import com.sfs.entities.Employee;
import com.sfs.entities.Groups; // Assuming the rename from Group_ to Groups
import com.sfs.entities.Plant;
import com.sfs.repository.DeviceRepository;
import com.sfs.repository.EmployeeRepository;
import com.sfs.repository.GroupRepository;
import com.sfs.repository.TimeLogRepository;
import com.sfs.service.DeviceService;
import com.sfs.service.PlantService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {

    @Mock
    protected EmployeeRepository employeeRepository;
    @Mock
    protected GroupRepository groupRepository;
    @Mock
    protected DeviceRepository deviceRepository;
    @Mock
    protected TimeLogRepository timeLogRepository;

    @Mock
    protected PlantService plantService;

    @InjectMocks 
    protected DeviceService deviceService; 

    protected Plant createPlant(String key, String location) {
        return new Plant(key, location);
    }

    protected Groups createGroup(Long id, String name, String plantKey) {
        Groups group = new Groups(name, plantKey);
        group.setId(id);
        group.setEmployees(new HashSet<Employee>());
        group.setDevices(new HashSet<>());
        return group;
    }

    protected Device createDevice(Long id, String type, int operators, String plantKey) {
        Device device = new Device(id, type, operators, plantKey);
        device.setGroups(new HashSet<>());
        return device;
    }

    protected Employee createEmployee(Long id, String first, String last, int year) {
        Employee employee = new Employee(first, last, year);
        employee.setId(id);
        employee.setGroups(new HashSet<>());
        return employee;
    }
}