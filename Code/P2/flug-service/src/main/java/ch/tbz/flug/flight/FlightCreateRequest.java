package ch.tbz.flug.flight;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FlightCreateRequest(
        @NotBlank(message = "Der Abflughafen darf nicht leer sein.") String departureAirportCode,
        @NotBlank(message = "Der Ankunftsflughafen darf nicht leer sein.") String arrivalAirportCode,
        @NotNull(message = "Die Startzeit ist erforderlich.") Instant departureAt,
        @NotNull(message = "Die Landezeit ist erforderlich.") Instant arrivalAt,
        @NotBlank(message = "Der Flugzeugtyp darf nicht leer sein.") String aircraftType) {
}
