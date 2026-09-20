package ch.tbz.flug.airport;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AirportClient {

    private final RestClient restClient;

    public AirportClient(RestClient.Builder builder, AirportClientProperties properties) {
        this.restClient = builder.baseUrl(properties.url()).build();
    }

    public List<AirportSummary> findAll() {
        return restClient.get()
                .uri("/api/airports")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
