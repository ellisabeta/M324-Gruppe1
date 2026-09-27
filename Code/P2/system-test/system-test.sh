#!/usr/bin/env bash
# Systemtest: prüft das Zusammenspiel beider Services über echte HTTP-Aufrufe.
# Erwartet laufende Services (docker compose up) auf den Ports 8081 und 8082.
set -uo pipefail

AIRPORT_URL="${AIRPORT_URL:-http://localhost:8081}"
FLIGHT_URL="${FLIGHT_URL:-http://localhost:8082}"
COMPOSE_FILE="${COMPOSE_FILE:-$(dirname "$0")/../docker-compose.yml}"
failures=0

wait_for() {
  local url=$1
  for _ in $(seq 1 60); do
    if curl -s -o /dev/null "$url"; then
      return 0
    fi
    sleep 2
  done
  echo "FEHLER: $url ist nach 120 s nicht erreichbar"
  exit 1
}

# check <Beschreibung> <erwarteter Status> <Methode> <URL> [JSON-Body]
check() {
  local name=$1 expected=$2 method=$3 url=$4 body=${5:-}
  local status
  if [ -n "$body" ]; then
    status=$(curl -s -o /tmp/response.json -w '%{http_code}' -X "$method" -H 'Content-Type: application/json' -d "$body" "$url")
  else
    status=$(curl -s -o /tmp/response.json -w '%{http_code}' -X "$method" "$url")
  fi
  if [ "$status" = "$expected" ]; then
    echo "OK    $name ($status)"
  else
    echo "FAIL  $name: erwartet $expected, erhalten $status"
    cat /tmp/response.json; echo
    failures=$((failures + 1))
  fi
}

flight() {
  echo "{\"departureAirportCode\":\"$1\",\"arrivalAirportCode\":\"$2\",\"departureAt\":\"2026-10-01T08:00:00Z\",\"arrivalAt\":\"$3\",\"aircraftType\":\"A320\"}"
}

echo "Warte auf Services ..."
wait_for "$AIRPORT_URL/api/airports"
wait_for "$FLIGHT_URL/api/flights"

echo "--- Flughafenservice"
check "Leere Datenbank liefert 404"         404 GET  "$AIRPORT_URL/api/airports"
check "Flughafen ZRH erstellen"             201 POST "$AIRPORT_URL/api/airports" '{"name":"Zürich","code":"ZRH","capacity":30000}'
check "Flughafen JFK erstellen"             201 POST "$AIRPORT_URL/api/airports" '{"name":"New York JFK","code":"JFK","capacity":50000}'
check "Doppeltes Kürzel wird abgelehnt"     409 POST "$AIRPORT_URL/api/airports" '{"name":"Zürich","code":"zrh","capacity":1}'
check "Ungültiges Kürzel wird abgelehnt"    400 POST "$AIRPORT_URL/api/airports" '{"name":"Test","code":"ZH","capacity":1}'
check "Flughäfen abrufen"                   200 GET  "$AIRPORT_URL/api/airports"

echo "--- Flugservice (ruft Flughafenservice auf)"
check "Gültiger Flug ZRH -> JFK"            201 POST "$FLIGHT_URL/api/flights" "$(flight ZRH JFK 2026-10-01T16:00:00Z)"
check "Unbekannter Flughafen liefert 404"   404 POST "$FLIGHT_URL/api/flights" "$(flight ZRH LAX 2026-10-01T16:00:00Z)"
check "Gleicher Flughafen liefert 400"      400 POST "$FLIGHT_URL/api/flights" "$(flight ZRH ZRH 2026-10-01T16:00:00Z)"
check "Landung vor Start liefert 400"       400 POST "$FLIGHT_URL/api/flights" "$(flight ZRH JFK 2026-10-01T07:00:00Z)"

echo "--- Ausfall Flughafenservice"
docker compose -f "$COMPOSE_FILE" stop flughafen-service > /dev/null 2>&1
check "Flughafenservice down liefert 502"   502 POST "$FLIGHT_URL/api/flights" "$(flight ZRH JFK 2026-10-01T16:00:00Z)"

echo
if [ "$failures" -gt 0 ]; then
  echo "Systemtest fehlgeschlagen: $failures Prüfung(en) nicht erfüllt"
  exit 1
fi
echo "Systemtest erfolgreich"
