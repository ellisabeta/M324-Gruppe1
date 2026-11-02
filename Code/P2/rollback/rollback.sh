#!/usr/bin/env bash
# Save a verified application pair, or restore it in the existing P2 Compose project.
set -euo pipefail
umask 077

# Paths and command arguments
HERE=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
BASE=$(cd "$HERE/.." && pwd)
action=${1:-}
project=${2:-p2}

[[ $action =~ ^(save|restore|status)$ && $project =~ ^[a-z0-9][a-z0-9_-]*$ ]] || {
  echo 'Usage: bash rollback.sh save|restore|status [compose-project, default: p2]' >&2
  exit 1
}

# Required command-line tools
for tool in docker jq curl flock sha256sum; do
  command -v "$tool" >/dev/null || {
    echo "Missing dependency: $tool" >&2
    exit 1
  }
done

# Saves local checkpoint and exclusive lock
state="$HERE/.state/$project"
mkdir -p "$state"
exec 9>"$state/lock"
flock -n 9 || {
  echo 'Another rollback/checkpoint is running.' >&2
  exit 1
}
checkpoint="$state/checkpoint.json"
compose=(
  docker compose
  --project-directory "$BASE"
  -p "$project"
  -f "$BASE/docker-compose.deploy.yml"
)
services=(flughafen-service flug-service)
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

log() {
  printf '%s %s\n' "$(date -u +%FT%TZ)" "$*" | tee -a "$state/events.log"
}

fail() {
  log "ERROR: $*"
  exit 1
}

config_hash=$(sha256sum "$BASE/docker-compose.deploy.yml" | cut -d ' ' -f1)

# Checks the two functions 1. calling Airport 2. send invalid flight
smoke() {
  local status

  status=$(curl -sS --max-time 5 -o "$work/airport.json" -w '%{http_code}' \
    http://127.0.0.1:8081/api/airports) || return 1
  case "$status" in
    200)
      jq -e 'type == "array"' "$work/airport.json" >/dev/null || return 1
      ;;
    404)
      jq -e '.status == 404 and .message == "Es sind noch keine Flughäfen vorhanden."' \
        "$work/airport.json" >/dev/null || return 1
      ;;
    *)
      return 1
      ;;
  esac

  status=$(curl -sS --max-time 5 -o "$work/flight.json" -w '%{http_code}' \
    -H 'Content-Type: application/json' \
    -d '{"departureAirportCode":"ZRH","arrivalAirportCode":"ZRH","departureAt":"2030-01-01T08:00:00Z","arrivalAt":"2030-01-01T10:00:00Z","aircraftType":"A320"}' \
    http://127.0.0.1:8082/api/flights) || return 1
  [[ $status == 400 ]] || return 1
  jq -e '.status == 400 and .message == "Abflug- und Ankunftsflughafen dürfen nicht gleich sein."' \
    "$work/flight.json" >/dev/null
}

running_image() {
  local cid value

  cid=$("${compose[@]}" ps -q "$1") || return 1
  [[ -n $cid && $cid != *$'\n'* ]] || return 1
  value=$(docker inspect --format '{{.Image}} {{.State.Running}}' "$cid") || return 1
  [[ $value =~ ^(sha256:[a-f0-9]{64})\ true$ ]] || return 1
  printf '%s\n' "${BASH_REMATCH[1]}"
}

# Show the saved checkpoint
if [[ $action == status ]]; then
  [[ -f $checkpoint ]] || fail 'No checkpoint. Run save while the application works.'
  cat "$checkpoint"
  exit 0
fi

# Save the current two docker images
if [[ $action == save ]]; then
  airport=$(running_image flughafen-service) || fail 'Airport container not running.'
  flight=$(running_image flug-service) || fail 'Flight container not running.'
  smoke || fail 'Smoke failed. Existing checkpoint was not replaced.'

  # Dedicated local tags keep these images referenced; restore still uses immutable IDs.
  docker image tag "$airport" "m324-rollback/$project-airport:saved"
  docker image tag "$flight" "m324-rollback/$project-flight:saved"
  jq -n --arg project "$project" --arg airport "$airport" --arg flight "$flight" \
    --arg config "$config_hash" --arg saved "$(date -u +%FT%TZ)" \
    '{project:$project,airport:$airport,flight:$flight,compose_sha256:$config,saved_at:$saved}' \
    > "$work/checkpoint.json"
  cp "$work/checkpoint.json" "$state/checkpoint.tmp"
  mv "$state/checkpoint.tmp" "$checkpoint"
  log 'SAVE_OK: verified image pair saved. This command did not deploy anything.'
  exit 0
fi

# Cheking the checkpoint and the saved image ids
[[ -f $checkpoint ]] || fail 'No checkpoint. Do not guess a previous version.'
jq -e --arg project "$project" --arg config "$config_hash" \
  '.project == $project and .compose_sha256 == $config and
   (.airport | test("^sha256:[a-f0-9]{64}$")) and
   (.flight | test("^sha256:[a-f0-9]{64}$"))' "$checkpoint" >/dev/null \
  || fail 'Checkpoint invalid or Compose configuration changed. Investigate first.'
airport=$(jq -r '.airport' "$checkpoint")
flight=$(jq -r '.flight' "$checkpoint")

# Restoring and checking the services that start the images
docker image inspect "$airport" >/dev/null || fail 'Saved airport image missing locally.'
docker image inspect "$flight" >/dev/null || fail 'Saved flight image missing locally.'
jq -n --arg airport "$airport" --arg flight "$flight" \
  '{services:{"flughafen-service":{image:$airport},"flug-service":{image:$flight}}}' \
  > "$work/images.json"
log 'RESTORE_START: restoring both saved application images; MongoDB is excluded.'
if ! "${compose[@]}" -f "$work/images.json" up -d --no-deps --no-build --pull never "${services[@]}"; then
  log 'RESTORE_FAILED: Compose error. Check containers/logs; checkpoint retained. Retry restore after repair.'
  exit 2
fi

# Approximately 90 seconds plus bounded HTTP/Docker command execution time.
for ((attempt = 1; attempt <= 30; attempt++)); do
  if [[ $(running_image flughafen-service) == "$airport" ]] && \
     [[ $(running_image flug-service) == "$flight" ]] && smoke; then
    log 'RESTORE_OK: saved images running and smoke passed. Checkpoint retained.'
    exit 0
  fi
  sleep 3
done
log 'RESTORE_FAILED: smoke/image verification failed. Investigate Docker, DB and logs; checkpoint retained.'
exit 2
