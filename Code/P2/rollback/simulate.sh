#!/usr/bin/env bash
# Executes the actual rollback.sh in a temporary project with fake docker/curl binaries.
set -euo pipefail

# Isolated test project
HERE=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
SIM_ROOT=$(mktemp -d)
trap 'rm -rf "$SIM_ROOT"' EXIT
export SIM_ROOT
mkdir -p "$SIM_ROOT/bin" "$SIM_ROOT/project/rollback"
cp "$HERE/rollback.sh" "$SIM_ROOT/project/rollback/"
cp "$HERE/../docker-compose.deploy.yml" "$SIM_ROOT/project/"
printf 'sha256:%064d\n' 1 > "$SIM_ROOT/old-airport"
printf 'sha256:%064d\n' 2 > "$SIM_ROOT/old-flight"
cp "$SIM_ROOT/old-airport" "$SIM_ROOT/flughafen-service"
cp "$SIM_ROOT/old-flight" "$SIM_ROOT/flug-service"
echo 'simulated MongoDB data: ZRH JFK existing-flight' > "$SIM_ROOT/db"
cp "$SIM_ROOT/db" "$SIM_ROOT/db-before"

# Fake Docker executable
cat > "$SIM_ROOT/bin/docker" <<'FAKE'
#!/usr/bin/env bash
set -euo pipefail
printf '%s\n' "$*" >> "$SIM_ROOT/commands"
case "$1 $2" in
  'image tag')
    exit 0
    ;;
  'image inspect')
    [[ ! -f "$SIM_ROOT/missing" ]]
    exit $?
    ;;
  'inspect --format')
    service=${@: -1}
    printf '%s true\n' "$(cat "$SIM_ROOT/$service")"
    exit 0
    ;;
esac
[[ $1 == compose ]] || exit 90
shift
override=''
while (($#)); do
  case "$1" in
    --project-directory | -p)
      shift 2
      ;;
    -f)
      override=$2
      shift 2
      ;;
    ps)
      printf '%s\n' "${@: -1}"
      exit 0
      ;;
    up)
      [[ "$*" == 'up -d --no-deps --no-build --pull never flughafen-service flug-service' ]] || exit 91
      jq -r '.services["flughafen-service"].image' "$override" > "$SIM_ROOT/flughafen-service"
      # Simulate a partial update before the error.
      [[ ! -f "$SIM_ROOT/compose-fail" ]] || exit 1
      jq -r '.services["flug-service"].image' "$override" > "$SIM_ROOT/flug-service"
      exit 0
      ;;
    *)
      exit 92
      ;;
  esac
done
exit 93
FAKE

# Fake HTTP responses
cat > "$SIM_ROOT/bin/curl" <<'FAKE'
#!/usr/bin/env bash
set -euo pipefail
out=''
url=${@: -1}
while (($#)); do
  if [[ $1 == -o ]]; then
    out=$2
    shift 2
  else
    shift
  fi
done
if [[ -f "$SIM_ROOT/http-fail" ]]; then
  echo '{}' > "$out"
  printf 500
elif [[ $url == */api/airports ]]; then
  echo '[]' > "$out"
  printf 200
else
  printf '%s\n' '{"status":400,"message":"Abflug- und Ankunftsflughafen dürfen nicht gleich sein."}' > "$out"
  printf 400
fi
FAKE

# Skip waiting only inside the simulation
cat > "$SIM_ROOT/bin/sleep" <<'FAKE'
#!/usr/bin/env bash
exit 0
FAKE
chmod +x "$SIM_ROOT/bin/"*
export PATH="$SIM_ROOT/bin:$PATH"
script="$SIM_ROOT/project/rollback/rollback.sh"
checkpoint="$SIM_ROOT/project/rollback/.state/p2/checkpoint.json"

# Assert the expected failure code
expect_failure() {
  local expected=$1 code=0
  shift
  "$@" || code=$?
  [[ $code == "$expected" ]] || {
    echo "FAIL expected $expected, got $code"
    exit 1
  }
}

# Test scenarios
echo 'SIMULATION: real Bash rollback script; Docker, HTTP and waiting are replaced.'
expect_failure 1 bash "$script" restore
echo 'PASS 1: restore without checkpoint rejected'

bash "$script" save
cp "$checkpoint" "$SIM_ROOT/checkpoint-before"
echo 'PASS 2: working image pair saved'

touch "$SIM_ROOT/http-fail"
expect_failure 1 bash "$script" save
cmp "$checkpoint" "$SIM_ROOT/checkpoint-before"
rm "$SIM_ROOT/http-fail"
echo 'PASS 3: unhealthy state cannot overwrite checkpoint'

printf 'sha256:%064d\n' 3 > "$SIM_ROOT/flughafen-service"
printf 'sha256:%064d\n' 4 > "$SIM_ROOT/flug-service"
bash "$script" restore
cmp "$SIM_ROOT/flughafen-service" "$SIM_ROOT/old-airport"
cmp "$SIM_ROOT/flug-service" "$SIM_ROOT/old-flight"
cmp "$SIM_ROOT/db" "$SIM_ROOT/db-before"
echo 'PASS 4: both old images restored; simulated data unchanged'

touch "$SIM_ROOT/missing"
count=$(wc -l < "$SIM_ROOT/commands")
expect_failure 1 bash "$script" restore
# Image preflight fails before compose up.
tail -n +"$((count+1))" "$SIM_ROOT/commands" > "$SIM_ROOT/new-commands"
if grep -q ' up ' "$SIM_ROOT/new-commands"; then
  exit 1
fi
rm "$SIM_ROOT/missing"
echo 'PASS 5: missing image rejected before changes'

touch "$SIM_ROOT/compose-fail"
expect_failure 2 bash "$script" restore
cmp "$checkpoint" "$SIM_ROOT/checkpoint-before"
rm "$SIM_ROOT/compose-fail"
bash "$script" restore
echo 'PASS 6: partial Compose failure reported; retry recovers'

touch "$SIM_ROOT/http-fail"
expect_failure 2 bash "$script" restore
cmp "$checkpoint" "$SIM_ROOT/checkpoint-before"
rm "$SIM_ROOT/http-fail"
echo 'PASS 7: failed verification is not reported as success'

echo '# changed configuration' >> "$SIM_ROOT/project/docker-compose.deploy.yml"
expect_failure 1 bash "$script" restore
echo 'PASS 8: changed Compose configuration rejected'

echo 'RESULT: 8 scenarios passed. No real containers or database were used.'
