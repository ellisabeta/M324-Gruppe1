package ch.tbz.flughafen.airport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AirportCreateRequest(
        @NotBlank(message = "Der Name darf nicht leer sein.") String name,
        @NotBlank(message = "Das Kürzel darf nicht leer sein.")
        @Pattern(regexp = "[A-Za-z]{3}", message = "Das Kürzel muss aus genau drei Buchstaben bestehen.") String code,
        @Min(value = 1, message = "Die Kapazität muss grösser als 0 sein.") int capacity) {
}
