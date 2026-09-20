package ch.tbz.flughafen.airport;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AirportRepository extends MongoRepository<Airport, String> {

    boolean existsByCodeIgnoreCase(String code);
}
