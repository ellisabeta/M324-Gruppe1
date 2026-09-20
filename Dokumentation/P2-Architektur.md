# P2 – Architektur und Schnittstellen

## Architektur

P2 besteht aus zwei getrennten Spring-Boot-Microservices. Jeder Service besitzt eine eigene MongoDB-Datenbank und wird unabhängig gestartet.

- `flughafen-service`: Port `8081`, Datenbank `flughafen_db`
- `flug-service`: Port `8082`, Datenbank `flug_db`

Der Flugservice prüft Flughäfen über die API des Flughafenservices. Er greift nicht direkt auf dessen Datenbank zu.

## Endpunkte

| Service | Methode | Endpoint | Zweck |
|---|---|---|---|
| Flughäfen | POST | `/api/airports` | Flughafen erstellen |
| Flughäfen | GET | `/api/airports` | Alle Flughäfen abrufen |
| Flüge | POST | `/api/flights` | Flug erstellen |

## Datenmodelle

Ein Flughafen enthält `id`, `name`, `code`, `capacity` und `createdAt`. Die ID und das Erstellungsdatum werden automatisch erzeugt.

Ein Flug enthält `id`, `departureAirportCode`, `arrivalAirportCode`, `departureAt`, `arrivalAt`, `aircraftType` und `createdAt`.

## Fehler und HTTP-Statuscodes

- `201 Created`: erfolgreich erstellt
- `200 OK`: erfolgreich abgerufen
- `400 Bad Request`: ungültige Eingaben
- `404 Not Found`: Flughafen nicht gefunden
- `409 Conflict`: Flughafenkürzel bereits vorhanden
- `502 Bad Gateway`: Flughafenservice nicht erreichbar
- `500 Internal Server Error`: unerwarteter Fehler
