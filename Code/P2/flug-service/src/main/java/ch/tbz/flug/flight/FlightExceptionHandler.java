package ch.tbz.flug.flight;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

record FlightApiError(Instant timestamp, int status, String error, String message) {
}

@RestControllerAdvice
public class FlightExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<FlightApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Die Eingaben sind ungültig.");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({SameAirportException.class, InvalidFlightTimeException.class})
    ResponseEntity<FlightApiError> handleBusinessError(RuntimeException exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UnknownAirportException.class)
    ResponseEntity<FlightApiError> handleUnknownAirport(UnknownAirportException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    ResponseEntity<FlightApiError> handleAirportServiceError(RestClientException exception) {
        return error(HttpStatus.BAD_GATEWAY, "Der Flughafenservice ist nicht erreichbar.");
    }

    private ResponseEntity<FlightApiError> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new FlightApiError(Instant.now(), status.value(), status.getReasonPhrase(), message));
    }
}
