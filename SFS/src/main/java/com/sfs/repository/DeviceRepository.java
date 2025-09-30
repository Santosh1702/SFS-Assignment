package com.sfs.repository;

import com.sfs.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    List<Device> findByPlantKey(String plantKey);
    
    @Query("SELECT d FROM Device d JOIN FETCH d.groups g WHERE d.deviceId = :deviceId")
    Optional<Device> findByDeviceIdWithGroups(@Param("deviceId") Long deviceId);
}