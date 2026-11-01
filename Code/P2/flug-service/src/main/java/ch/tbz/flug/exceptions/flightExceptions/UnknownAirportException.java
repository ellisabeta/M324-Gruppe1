package ch.tbz.flug.exceptions.flightExceptions;

public class UnknownAirportException extends RuntimeException {
    public UnknownAirportException(String code) {
        super("Der Flughafen " + code + " wurde nicht gefunden.");
    }
}
