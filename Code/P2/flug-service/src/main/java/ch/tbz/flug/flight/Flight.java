package ch.tbz.flug.flight;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "flights")
public class Flight {

    @Id
    private String id;
    private String departureAirportCode;
    private String arrivalAirportCode;
    private Instant departureAt;
    private Instant arrivalAt;
    private String aircraftType;
    private Instant createdAt;

    protected Flight() {
    }

    public Flight(String id, String departureAirportCode, String arrivalAirportCode,
                  Instant departureAt, Instant arrivalAt, String aircraftType, Instant createdAt) {
        this.id = id;
        this.departureAirportCode = departureAirportCode;
        this.arrivalAirportCode = arrivalAirportCode;
        this.departureAt = departureAt;
        this.arrivalAt = arrivalAt;
        this.aircraftType = aircraftType;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public String getArrivalAirportCode() { return arrivalAirportCode; }
    public Instant getDepartureAt() { return departureAt; }
    public Instant getArrivalAt() { return arrivalAt; }
    public String getAircraftType() { return aircraftType; }
    public Instant getCreatedAt() { return createdAt; }
}
