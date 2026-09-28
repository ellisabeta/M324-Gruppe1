package ch.tbz.flug.flight;

class SameAirportException extends RuntimeException {
    SameAirportException() { super("Abflug- und Ankunftsflughafen dürfen nicht gleich sein."); }
}

class InvalidFlightTimeException extends RuntimeException {
    InvalidFlightTimeException() { super("Die Landezeit muss nach der Startzeit liegen."); }
}

class UnknownAirportException extends RuntimeException {
    UnknownAirportException(String code) { super("Der Flughafen " + code + " wurde nicht gefunden."); }
}
