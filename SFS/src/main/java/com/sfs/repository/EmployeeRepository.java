package com.sfs.repository;

import com.sfs.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Import this
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e JOIN FETCH e.groups WHERE e.id = :id")
    Optional<Employee> findByIdWithGroups(Long id);

    Optional<Employee> findByFirstNameAndLastName(String firstName, String lastName);

    long countByCurrentDeviceDeviceId(Long deviceId);
    
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.currentDevice d WHERE e.id = :id")
    Optional<Employee> findByIdWithCurrentDevice(Long id);
}