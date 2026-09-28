package ch.tbz.flughafen.airport;

import java.time.Instant;

public record AirportResponse(
        String id,
        String name,
        String code,
        int capacity,
        Instant createdAt) {

    public static AirportResponse from(Airport airport) {
        return new AirportResponse(
                airport.getId(),
                airport.getName(),
                airport.getCode(),
                airport.getCapacity(),
                airport.getCreatedAt());
    }
}
