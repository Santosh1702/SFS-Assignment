package com.sfs.service;

import com.sfs.entities.Groups;
import com.sfs.entities.Device;
import com.sfs.entities.Employee;
import com.sfs.entities.TimeLog;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest extends AbstractServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, groupRepository, deviceService, timeLogRepository);
    }
    
    private final Long EMPLOYEE_ID = 10L;
    private final Long DEVICE_ID_A = 100L;
    private final Long DEVICE_ID_B = 200L;
    private final Long GROUP_ID = 1L;
    private final String PLANT_KEY = "PLANT_X";

    @Test
    void loginToDevice_success() {
        Employee employee = createEmployee(EMPLOYEE_ID, "John", "Doe", 1990);
        Device deviceA = createDevice(DEVICE_ID_A, "Drill", 2, PLANT_KEY);
        Groups group = createGroup(GROUP_ID, "Drill Team", PLANT_KEY);

        employee.setGroups(Set.of(group));
        group.setDevices(Set.of(deviceA));

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(DEVICE_ID_A)).thenReturn(Optional.of(deviceA));
        when(employeeRepository.countByCurrentDeviceDeviceId(DEVICE_ID_A)).thenReturn(0L); // Capacity check passes
        when(timeLogRepository.save(any(TimeLog.class))).thenAnswer(i -> i.getArguments()[0]);

        TimeLog result = employeeService.loginToDevice(EMPLOYEE_ID, DEVICE_ID_A);

        assertNotNull(result);
        assertEquals(DEVICE_ID_A, employee.getCurrentDevice().getDeviceId());
        verify(employeeRepository, times(1)).save(employee);
        verify(timeLogRepository, times(1)).save(any(TimeLog.class));
    }

    @Test
    void loginToDevice_failure_alreadyLoggedIn() {
        Employee employee = createEmployee(EMPLOYEE_ID, "John", "Doe", 1990);
        Device deviceA = createDevice(DEVICE_ID_A, "Drill", 1, PLANT_KEY);

        employee.setCurrentDevice(deviceA); 
        
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(DEVICE_ID_B)).thenReturn(Optional.of(createDevice(DEVICE_ID_B, "Lathe", 1, PLANT_KEY)));

        assertThrows(IllegalStateException.class, () -> {
            employeeService.loginToDevice(EMPLOYEE_ID, DEVICE_ID_B);
        });
        verify(timeLogRepository, never()).save(any());
    }

    @Test
    void loginToDevice_failure_unauthorizedGroup() {

        Employee employee = createEmployee(EMPLOYEE_ID, "John", "Doe", 1990);
        Device deviceA = createDevice(DEVICE_ID_A, "Drill", 1, PLANT_KEY);
        Groups groupNotAssigned = createGroup(GROUP_ID, "Janitors", PLANT_KEY);

        employee.setGroups(Set.of(groupNotAssigned));
        groupNotAssigned.setDevices(new HashSet<Device>()); 

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(DEVICE_ID_A)).thenReturn(Optional.of(deviceA));
        
        assertThrows(IllegalStateException.class, () -> {
            employeeService.loginToDevice(EMPLOYEE_ID, DEVICE_ID_A);
        });
        verify(timeLogRepository, never()).save(any());
    }
    
    @Test
    void loginToDevice_failure_capacityExceeded() {

        Employee employee = createEmployee(EMPLOYEE_ID, "John", "Doe", 1990);
        Device deviceA = createDevice(DEVICE_ID_A, "Drill", 1, PLANT_KEY); // Capacity is 1
        Groups group = createGroup(GROUP_ID, "Drill Team", PLANT_KEY);
        
        employee.setGroups(Set.of(group));
        group.setDevices(Set.of(deviceA));

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(deviceRepository.findById(DEVICE_ID_A)).thenReturn(Optional.of(deviceA));
        
        when(employeeRepository.countByCurrentDeviceDeviceId(DEVICE_ID_A)).thenReturn(1L); 

        assertThrows(IllegalStateException.class, () -> {
            employeeService.loginToDevice(EMPLOYEE_ID, DEVICE_ID_A);
        });
        verify(timeLogRepository, never()).save(any());
    }

    @Test
    void logoutFromDevice_success() {

        Employee employee = createEmployee(EMPLOYEE_ID, "John", "Doe", 1990);
        Device deviceA = createDevice(DEVICE_ID_A, "Drill", 1, PLANT_KEY);
        
        employee.setCurrentDevice(deviceA); 
        TimeLog activeLog = new TimeLog(employee, deviceA, Instant.now().minus(Duration.ofMinutes(10)));
        activeLog.setId(100L);

        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(timeLogRepository.findByEmployeeIdAndDeviceDeviceIdAndEndTimeIsNull(EMPLOYEE_ID, DEVICE_ID_A)).thenReturn(Optional.of(activeLog));
        when(timeLogRepository.save(any(TimeLog.class))).thenAnswer(i -> i.getArguments()[0]);

        TimeLog result = employeeService.logoutFromDevice(EMPLOYEE_ID, DEVICE_ID_A);

        assertNotNull(result.getEndTime());
        assertNull(employee.getCurrentDevice());
        verify(employeeRepository, times(1)).save(employee);
        verify(timeLogRepository, times(1)).save(activeLog);
    }
}