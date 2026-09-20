package ch.tbz.flughafen.airport;

public class NoAirportsException extends RuntimeException {

    public NoAirportsException() {
        super("Es sind noch keine Flughäfen vorhanden.");
    }
}
