package co.cetad.umas.gateway.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping(value = "/geofences", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, Object>> geoEventFallback() {
        return Mono.just(Map.of(
                "error", "Service Unavailable",
                "service", "Geofence Service",
                "message", "El servicio de geocercas no está disponible temporalmente",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping(value = "/drones", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, Object>> dronesFallback() {
        return Mono.just(Map.of(
                "error", "Service Unavailable",
                "service", "Resource Service",
                "message", "El servicio de resource no está disponible temporalmente",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping(value = "/routes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, Object>> routesFallback() {
        return Mono.just(Map.of(
                "error", "Service Unavailable",
                "service", "Route Service",
                "message", "El servicio de rutas no está disponible temporalmente",
                "timestamp", Instant.now().toString()
        ));
    }

}