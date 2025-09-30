package com.sfs.service;

import com.sfs.entities.Employee;
import com.sfs.entities.Groups;
import com.sfs.entities.Device;
import com.sfs.entities.TimeLog;
import com.sfs.repository.EmployeeRepository;
import com.sfs.repository.GroupRepository;
import com.sfs.repository.TimeLogRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final GroupRepository groupRepository;
    private final DeviceService deviceService; 
    private final TimeLogRepository timeLogRepository;

    public EmployeeService(EmployeeRepository employeeRepository, GroupRepository groupRepository, DeviceService deviceService, TimeLogRepository timeLogRepository) {
        this.employeeRepository = employeeRepository;
        this.groupRepository = groupRepository;
        this.deviceService = deviceService;
        this.timeLogRepository = timeLogRepository;
    }

    public Employee save(Employee employee) {
        if (employee.getYearOfBirth() > Year.now().getValue() - 16) {
            throw new IllegalArgumentException("Employee must be at least 16 years old.");
        }
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee assignToGroups(Long employeeId, Set<Long> groupIds) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        Set<Groups> newGroups = new HashSet<>();
        for (Long groupId : groupIds) {
            Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupId));
            newGroups.add(group);
        }

        employee.setGroups(newGroups);
        return employeeRepository.save(employee);
    }

    public TimeLog loginToDevice(Long employeeId, Long deviceId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found."));

        Device device = deviceService.findById(deviceId)
            .orElseThrow(() -> new EntityNotFoundException("Device not found."));

        if (employee.getCurrentDevice() != null) {
            throw new IllegalStateException("Employee is already logged into device ID: " + employee.getCurrentDevice().getDeviceId());
        }

        boolean canOperate = employee.getGroups().stream()
            .anyMatch(g -> g.getDevices().contains(device));

        if (!canOperate) {
            throw new IllegalStateException("Employee's group is not assigned to device ID: " + deviceId);
        }

        long currentOperators = employeeRepository.countByCurrentDeviceDeviceId(deviceId);
        if (currentOperators >= device.getRequiredOperators()) {
            throw new IllegalStateException("Device ID: " + deviceId + " has reached its maximum operator capacity (" + device.getRequiredOperators() + ")");
        }

        employee.setCurrentDevice(device);
        employeeRepository.save(employee);

        TimeLog timeLog = new TimeLog(employee, device, Instant.now());
        return timeLogRepository.save(timeLog);
    }

    public TimeLog logoutFromDevice(Long employeeId, Long deviceId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found."));

        if (employee.getCurrentDevice() == null || !employee.getCurrentDevice().getDeviceId().equals(deviceId)) {
            throw new IllegalStateException("Employee is not currently logged into device ID: " + deviceId);
        }

        TimeLog timeLog = timeLogRepository.findByEmployeeIdAndDeviceDeviceIdAndEndTimeIsNull(employeeId, deviceId)
                .orElseThrow(() -> new EntityNotFoundException("No active session found for employee on device."));

        timeLog.setEndTime(Instant.now());
        timeLogRepository.save(timeLog);

        employee.setCurrentDevice(null);
        employeeRepository.save(employee);

        return timeLog;
    }
}