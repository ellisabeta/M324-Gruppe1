package ch.tbz.flughafen.airport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AirportApiIT {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private AirportRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void storesAirportAndReturnsItFromDatabase() {
        ResponseEntity<AirportResponse> created = rest.postForEntity("/api/airports",
                json("{\"name\":\"Zürich\",\"code\":\"zrh\",\"capacity\":30000}"), AirportResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody().code()).isEqualTo("ZRH");
        assertThat(repository.count()).isEqualTo(1);

        ResponseEntity<AirportResponse[]> all = rest.getForEntity("/api/airports", AirportResponse[].class);

        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).extracting(AirportResponse::code).containsExactly("ZRH");
    }

    @Test
    void rejectsDuplicateCodeAgainstStoredAirport() {
        rest.postForEntity("/api/airports", json("{\"name\":\"Zürich\",\"code\":\"ZRH\",\"capacity\":30000}"), String.class);

        ResponseEntity<String> duplicate = rest.postForEntity("/api/airports",
                json("{\"name\":\"Zürich Kloten\",\"code\":\"zrh\",\"capacity\":100}"), String.class);

        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void returnsNotFoundForEmptyDatabase() {
        ResponseEntity<String> response = rest.getForEntity("/api/airports", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
