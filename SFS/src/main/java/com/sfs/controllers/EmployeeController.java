package com.sfs.controllers;

import com.sfs.entities.Employee;
import com.sfs.entities.TimeLog;
import com.sfs.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.save(employee);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{employeeId}/groups")
    public ResponseEntity<Employee> assignGroups(@PathVariable Long employeeId,
                                                 @RequestBody Map<String, Set<Long>> payload) {
        Set<Long> groupIds = payload.getOrDefault("groupIds", Collections.emptySet());
        try {
            Employee updatedEmployee = employeeService.assignToGroups(employeeId, groupIds);
            return ResponseEntity.ok(updatedEmployee);
        } catch (jakarta.persistence.EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/{employeeId}/login/{deviceId}")
    public ResponseEntity<TimeLog> loginToDevice(@PathVariable Long employeeId,
                                                 @PathVariable Long deviceId) {
        try {
            TimeLog timeLog = employeeService.loginToDevice(employeeId, deviceId);
            return new ResponseEntity<>(timeLog, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("X-Error-Reason", e.getMessage()).build();
        }
    }

    @PostMapping("/{employeeId}/logout/{deviceId}")
    public ResponseEntity<TimeLog> logoutFromDevice(@PathVariable Long employeeId,
                                                  @PathVariable Long deviceId) {
        try {
            TimeLog timeLog = employeeService.logoutFromDevice(employeeId, deviceId);
            return new ResponseEntity<>(timeLog, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("X-Error-Reason", e.getMessage()).build();
        }
    }
}