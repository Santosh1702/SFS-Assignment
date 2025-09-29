package com.sfs.repository;

import com.sfs.entities.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Required import
import org.springframework.stereotype.Repository;

import java.util.List; // <-- Required import
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {

    Optional<Groups> findByName(String name);
    
    @Query("SELECT g FROM Groups g JOIN FETCH g.employees JOIN FETCH g.devices")
    List<Groups> findAllWithEmployeesAndDevices();
}