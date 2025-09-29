package com.sfs.controllers;

import com.sfs.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/operation-time")
    public ResponseEntity<Map<String, Map<Long, Duration>>> getOperationTime() {
        Map<String, Map<Long, Duration>> report = statisticsService.getTotalOperationTimePerDeviceByGroup();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/utilization-rate")
    public ResponseEntity<Map<String, Double>> getUtilizationRate() {
        Map<String, Double> report = statisticsService.getUtilizationRateLast24Hours();
        return ResponseEntity.ok(report);
    }
}