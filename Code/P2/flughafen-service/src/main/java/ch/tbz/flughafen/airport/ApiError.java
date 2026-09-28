package ch.tbz.flughafen.airport;

import java.time.Instant;

public record ApiError(Instant timestamp, int status, String error, String message) {
}
