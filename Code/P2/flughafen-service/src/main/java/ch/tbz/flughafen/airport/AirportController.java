package ch.tbz.flughafen.airport;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService service;

    public AirportController(AirportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AirportResponse> create(@Valid @RequestBody AirportCreateRequest request) {
        AirportResponse response = AirportResponse.from(service.create(request));
        return ResponseEntity.created(URI.create("/api/airports/" + response.id())).body(response);
    }

    @GetMapping
    public List<AirportResponse> findAll() {
        return service.findAll().stream().map(AirportResponse::from).toList();
    }
}
