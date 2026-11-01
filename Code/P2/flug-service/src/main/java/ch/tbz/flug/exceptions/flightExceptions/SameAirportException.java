package ch.tbz.flug.exceptions.flightExceptions;

public class SameAirportException extends RuntimeException {
    public SameAirportException() {
        super("Abflug- und Ankunftsflughafen dürfen nicht gleich sein.");
    }
}
