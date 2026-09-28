package ch.tbz.flug.flight;

import java.time.Instant;

public record FlightResponse(
        String id,
        String departureAirportCode,
        String arrivalAirportCode,
        Instant departureAt,
        Instant arrivalAt,
        String aircraftType,
        Instant createdAt) {

    public static FlightResponse from(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getDepartureAirportCode(),
                flight.getArrivalAirportCode(),
                flight.getDepartureAt(),
                flight.getArrivalAt(),
                flight.getAircraftType(),
                flight.getCreatedAt());
    }
}
