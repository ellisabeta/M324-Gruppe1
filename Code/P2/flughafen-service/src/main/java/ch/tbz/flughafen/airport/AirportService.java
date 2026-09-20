package ch.tbz.flughafen.airport;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AirportService {

    private final AirportRepository repository;
    private final Clock clock;

    @Autowired
    public AirportService(AirportRepository repository) {
        this(repository, Clock.systemUTC());
    }

    AirportService(AirportRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Airport create(AirportCreateRequest request) {
        String code = request.code().toUpperCase(Locale.ROOT);
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateAirportCodeException(code);
        }

        Airport airport = new Airport(
                UUID.randomUUID().toString(),
                request.name().trim(),
                code,
                request.capacity(),
                Instant.now(clock));

        try {
            return repository.save(airport);
        } catch (DuplicateKeyException exception) {
            throw new DuplicateAirportCodeException(code);
        }
    }

    public List<Airport> findAll() {
        List<Airport> airports = repository.findAll();
        if (airports.isEmpty()) {
            throw new NoAirportsException();
        }
        return airports;
    }
}
