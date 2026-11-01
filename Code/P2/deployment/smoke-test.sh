#!/usr/bin/env bash
# Smoke Test nach Deployment: Prüft Erreichbarkeit und Kernfunktionen der Services.
set -euo pipefail

AIRPORT_URL="${AIRPORT_URL:-http://127.0.0.1:8081}"
FLIGHT_URL="${FLIGHT_URL:-http://127.0.0.1:8082}"

echo "=== Starte Smoke Test nach Deployment ==="
echo "Flughafen-Service: $AIRPORT_URL"
echo "Flug-Service:      $FLIGHT_URL"

wait_for_service() {
  local url=$1
  local name=$2
  echo "Warte auf Erreichbarkeit von $name ($url)..."
  for i in $(seq 1 30); do
    if curl -s -o /dev/null --max-time 2 "$url"; then
      echo "  -> $name ist erreichbar (Versuch $i)"
      return 0
    fi
    sleep 2
  done
  echo "FEHLER: $name unter $url hat nach 60 Sekunden nicht geantwortet." >&2
  return 1
}

wait_for_service "$AIRPORT_URL/api/airports" "Flughafen-Service"
wait_for_service "$FLIGHT_URL/api/flights" "Flug-Service"

echo ""
echo "--- 1. Health & Read-Prüfung: /api/airports ---"
STATUS_AIRPORTS=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 "$AIRPORT_URL/api/airports")
if [[ "$STATUS_AIRPORTS" =~ ^(200|404)$ ]]; then
  echo "OK: /api/airports antwortet mit HTTP $STATUS_AIRPORTS"
else
  echo "FEHLER: /api/airports antwortet mit unerwartetem HTTP-Status: $STATUS_AIRPORTS" >&2
  exit 1
fi

echo ""
echo "--- 2. Funktionstest: Flughafen ZRH anlegen ---"
CREATE_AIRPORT_STATUS=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 -X POST \
  -H 'Content-Type: application/json' \
  -d '{"name":"Zürich Smoke Test","code":"ZRH","capacity":35000}' \
  "$AIRPORT_URL/api/airports")

if [[ "$CREATE_AIRPORT_STATUS" == "201" || "$CREATE_AIRPORT_STATUS" == "409" ]]; then
  echo "OK: Flughafen anlegen (Status $CREATE_AIRPORT_STATUS - 201 erstellt / 409 existiert bereits)"
else
  echo "FEHLER: Unerwarteter Status beim Flughafen anlegen: $CREATE_AIRPORT_STATUS" >&2
  exit 1
fi

echo ""
echo "--- 3. Funktionstest: Flugservice Validierung (Smoke-Check) ---"
FLIGHT_STATUS=$(curl -s -o /dev/null -w '%{http_code}' --max-time 5 -X POST \
  -H 'Content-Type: application/json' \
  -d '{"departureAirportCode":"ZRH","arrivalAirportCode":"ZRH","departureAt":"2030-01-01T10:00:00Z","arrivalAt":"2030-01-01T12:00:00Z","aircraftType":"A320"}' \
  "$FLIGHT_URL/api/flights")

if [[ "$FLIGHT_STATUS" == "400" ]]; then
  echo "OK: Flugservice verarbeitet Request korrekt (HTTP 400 für gleiche Start/Ziel-Flughäfen)"
else
  echo "FEHLER: Flugservice lieferte Status $FLIGHT_STATUS (erwartet 400)" >&2
  exit 1
fi

echo ""
echo "=== Smoke Test erfolgreich abgeschlossen! ==="

