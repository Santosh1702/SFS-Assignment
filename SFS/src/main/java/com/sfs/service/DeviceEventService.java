package com.sfs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import jakarta.annotation.PostConstruct; 

@Service
public class DeviceEventService {

    private final WebClient webClient;

    @Value("${sfs.mock-api.base-url:https://mock-api.assessment.sfsdm.org}")
    private String baseUrl;

    private WebClient externalEventsClient;

    public DeviceEventService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void initializeClient() {
        this.externalEventsClient = webClient.mutate()
            .baseUrl(baseUrl + "/events")
            .build();
    }

    public Flux<String> streamDeviceEvents(Long deviceId) {
        String path = "/" + deviceId;

        return externalEventsClient.get()
            .uri(path)
             .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(String.class)
            .doOnError(e -> System.err.println("Error connecting to external SSE stream: " + e.getMessage()))
            .onErrorResume(e -> {
                return Flux.empty(); 
            });
    }
}