package com.sfs.repository;

import com.sfs.entities.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Required for @Query
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    Optional<TimeLog> findByEmployeeIdAndDeviceDeviceIdAndEndTimeIsNull(Long employeeId, Long deviceId);

    List<TimeLog> findByEmployeeId(Long employeeId);

    List<TimeLog> findByStartTimeBetween(Instant start, Instant end);
    
    @Query("SELECT tl FROM TimeLog tl JOIN FETCH tl.employee e JOIN FETCH e.groups JOIN FETCH tl.device")
    List<TimeLog> findAllWithAllRelationships();

    @Query("SELECT tl FROM TimeLog tl JOIN FETCH tl.employee e JOIN FETCH e.groups JOIN FETCH tl.device WHERE tl.startTime BETWEEN :start AND :end")
    List<TimeLog> findByTimeRangeWithAllRelationships(Instant start, Instant end);
}