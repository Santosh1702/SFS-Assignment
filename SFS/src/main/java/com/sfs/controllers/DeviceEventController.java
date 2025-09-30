package com.sfs.controllers;

import com.sfs.service.DeviceEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/events")
public class DeviceEventController {

    private final DeviceEventService deviceEventService;

    public DeviceEventController(DeviceEventService deviceEventService) {
        this.deviceEventService = deviceEventService;
    }

    /**
     * Proxies Server-Sent Events (SSE) from the external device API to the client.
     */
    @GetMapping(value = "/{deviceId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getDeviceEvents(@PathVariable Long deviceId) {
        return deviceEventService.streamDeviceEvents(deviceId);
    }
}