package ch.tbz.flug.flight;

import java.net.URI;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FlightResponse> create(@Valid @RequestBody FlightCreateRequest request) {
        FlightResponse response = FlightResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/flights/" + response.id())).body(response);
    }
}
