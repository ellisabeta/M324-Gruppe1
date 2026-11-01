package ch.tbz.flug.exceptions.flightExceptions;

public class InvalidFlightTimeException extends RuntimeException {
    public InvalidFlightTimeException() {
        super("Die Landezeit muss nach der Startzeit liegen.");
    }
}
