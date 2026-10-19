package ch.tbz.flug.flight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ch.tbz.flug.airport.AirportClient;
import ch.tbz.flug.airport.AirportSummary;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightApiIT {

    private static final String VALID_FLIGHT = "{\"departureAirportCode\":\"zrh\",\"arrivalAirportCode\":\"JFK\","
            + "\"departureAt\":\"2026-10-01T08:00:00Z\",\"arrivalAt\":\"2026-10-01T16:00:00Z\",\"aircraftType\":\"A320\"}";

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private FlightRepository repository;

    @MockBean
    private AirportClient airportClient;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void storesValidFlightInDatabase() {
        when(airportClient.findAll()).thenReturn(List.of(
                new AirportSummary("1", "Zürich", "ZRH", 30000),
                new AirportSummary("2", "New York", "JFK", 50000)));

        ResponseEntity<FlightResponse> response = rest.postForEntity("/api/flights", json(VALID_FLIGHT), FlightResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Flight stored = repository.findById(response.getBody().id()).orElseThrow();
        assertThat(stored.getDepartureAirportCode()).isEqualTo("ZRH");
        assertThat(stored.getArrivalAirportCode()).isEqualTo("JFK");
        assertThat(stored.getAircraftType()).isEqualTo("A320");
    }

    @Test
    void doesNotStoreFlightWithUnknownAirport() {
        when(airportClient.findAll()).thenReturn(List.of(new AirportSummary("1", "Zürich", "ZRH", 30000)));

        ResponseEntity<String> response = rest.postForEntity("/api/flights", json(VALID_FLIGHT), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(repository.count()).isZero();
    }

    @Test
    void returnsBadGatewayWhenAirportServiceIsDown() {
        when(airportClient.findAll()).thenThrow(new ResourceAccessException("Connection refused"));

        ResponseEntity<String> response = rest.postForEntity("/api/flights", json(VALID_FLIGHT), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(repository.count()).isZero();
    }

    private HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
