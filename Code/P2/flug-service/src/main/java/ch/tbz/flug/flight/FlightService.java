package ch.tbz.flug.flight;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.tbz.flug.airport.AirportClient;
import ch.tbz.flug.airport.AirportSummary;

@Service
public class FlightService {

    private final FlightRepository repository;
    private final AirportClient airportClient;
    private final Clock clock;

    @Autowired
    public FlightService(FlightRepository repository, AirportClient airportClient) {
        this(repository, airportClient, Clock.systemUTC());
    }

    FlightService(FlightRepository repository, AirportClient airportClient, Clock clock) {
        this.repository = repository;
        this.airportClient = airportClient;
        this.clock = clock;
    }

    public Flight create(FlightCreateRequest request) {
        String departureCode = request.departureAirportCode().toUpperCase(Locale.ROOT);
        String arrivalCode = request.arrivalAirportCode().toUpperCase(Locale.ROOT);

        if (departureCode.equals(arrivalCode)) {
            throw new SameAirportException();
        }
        if (!request.arrivalAt().isAfter(request.departureAt())) {
            throw new InvalidFlightTimeException();
        }

        List<AirportSummary> airports = airportClient.findAll();
        boolean departureExists = airports.stream()
                .anyMatch(airport -> departureCode.equalsIgnoreCase(airport.code()));
        boolean arrivalExists = airports.stream()
                .anyMatch(airport -> arrivalCode.equalsIgnoreCase(airport.code()));

        if (!departureExists) {
            throw new UnknownAirportException(departureCode);
        }
        if (!arrivalExists) {
            throw new UnknownAirportException(arrivalCode);
        }

        Flight flight = new Flight(
                UUID.randomUUID().toString(),
                departureCode,
                arrivalCode,
                request.departureAt(),
                request.arrivalAt(),
                request.aircraftType().trim(),
                Instant.now(clock));
        return repository.save(flight);
    }
}
