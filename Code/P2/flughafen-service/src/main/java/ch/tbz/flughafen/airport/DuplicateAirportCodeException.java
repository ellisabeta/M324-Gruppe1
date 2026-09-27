package ch.tbz.flughafen.airport;

public class DuplicateAirportCodeException extends RuntimeException {

    public DuplicateAirportCodeException(String code) {
        super("Das Flughafenkürzel " + code + " ist bereits vorhanden.");
    }
}
