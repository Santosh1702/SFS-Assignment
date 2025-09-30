package com.sfs.service;

import com.sfs.entities.Groups;
import com.sfs.entities.TimeLog;
import com.sfs.repository.GroupRepository;
import com.sfs.repository.TimeLogRepository;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final TimeLogRepository timeLogRepository;
    private final GroupRepository groupRepository;

    public StatisticsService(TimeLogRepository timeLogRepository, GroupRepository groupRepository) {
        this.timeLogRepository = timeLogRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Calculates the total operation time per device by group.
     * This method executes synchronously, performing blocking JPA calls.
     * @return Map<String, Map<Long, Duration>> Map<GroupName, Map<DeviceId, TotalDuration>>
     */
    public Map<String, Map<Long, Duration>> getTotalOperationTimePerDeviceByGroup() {

        List<TimeLog> logs = timeLogRepository.findAllWithAllRelationships();

        Map<String, Map<Long, Duration>> groupDeviceTimeMap = new HashMap<>();

        for (TimeLog log : logs) {

            if (log.getEndTime() == null) continue;

            Duration duration = Duration.between(log.getStartTime(), log.getEndTime());

            Long deviceId = log.getDevice().getDeviceId();

            log.getEmployee().getGroups().forEach(group -> {
                String groupName = group.getName();
                groupDeviceTimeMap
                    .computeIfAbsent(groupName, k -> new HashMap<>())
                    .merge(deviceId, duration, Duration::plus);
            });
        }
        return groupDeviceTimeMap;
    }

    /**
     * Calculates the device utilization rate per group for the last 24 hours.
     * Rate = (Total employee-seconds logged into group devices) / (Max possible employee-seconds).
     * @return Map<String, Double> Map<GroupName, UtilizationRate (0.0 to 1.0)>
     */
    public Map<String, Double> getUtilizationRateLast24Hours() {
        Instant end = Instant.now();
        Instant start = end.minus(Duration.ofHours(24));
        long periodSeconds = Duration.ofHours(24).getSeconds();

        List<TimeLog> logs = timeLogRepository.findByTimeRangeWithAllRelationships(start, end);

        List<Groups> groups = groupRepository.fetchGroupsWithAllRelationships();

        Map<String, Double> groupUtilization = new HashMap<>();

        for (Groups group : groups) {

            int groupSize = group.getEmployees().size(); 
            
            if (groupSize == 0) {
                groupUtilization.put(group.getName(), 0.0);
                continue;
            }

            long totalEmployeeSeconds = 0;

            for (TimeLog log : logs) {

                boolean isRelevantLog = log.getEmployee().getGroups().contains(group) && group.getDevices().contains(log.getDevice());

                if (isRelevantLog) {
                    Instant logStart = log.getStartTime();
                    Instant logEnd = log.getEndTime() != null ? log.getEndTime() : end; 
                    Instant effectiveStart = logStart.isAfter(start) ? logStart : start;
                    Instant effectiveEnd = logEnd.isBefore(end) ? logEnd : end;

                    if (effectiveStart.isBefore(effectiveEnd)) {
                        totalEmployeeSeconds += Duration.between(effectiveStart, effectiveEnd).getSeconds();
                    }
                }
            }

            double maxPossibleTime = (double) groupSize * periodSeconds;
            
            double utilizationRate = maxPossibleTime > 0 ? (double) totalEmployeeSeconds / maxPossibleTime : 0.0;
            
            groupUtilization.put(group.getName(), utilizationRate);
        }

        return groupUtilization;
    }
}