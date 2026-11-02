# P4 – Continuous Deployment

## 1. Gewählte Deployment Variante
Wir verwenden **Docker Compose**, um den Flughafenservice, den Flugservice und MongoDB gemeinsam als Container-Stack zu starten.
- **Docker** stellt dafür die standardisierte Ausführungsumgebung bereit (lokal mit Docker Desktop, auf Servern oder in CI-Runnern).
- **Deployment-Konfiguration:** Für das produktive Deployment nutzen wir [`Code/P2/docker-compose.deploy.yml`](../Code/P2/docker-compose.deploy.yml). Im Unterschied zur Entwicklungs-Compose-Datei wird hier **nicht** neu gebaut, sondern die vorab gebauten und getesteten versionierten Docker-Images aus der **GitHub Container Registry (GHCR)** bezogen.
- Die Services, Ports und Umgebungsvariablen sind zentral definiert:
  - `flughafen-service`: Port 8081, Anbindung an containerisierte MongoDB
  - `flug-service`: Port 8082, Anbindung an containerisierte MongoDB und interne Kommunikation mit `flughafen-service:8081`
  - `mongo`: Port 27017 (offizielles Image `mongo:7.0`)

Diese Variante passt optimal zu unserem Projekt: Die Services sind modular getrennt, und mit Docker Compose kann der gesamte Stack mit einem einzigen, deklarativen Befehl deterministisch hochgefahren werden:
```bash
docker compose -f Code/P2/docker-compose.deploy.yml up -d
```

---

## 2. Automatisierte Pipeline (Delivery & Deployment)

Unsere GitHub Actions Pipeline ([`.github/workflows/ci.yml`](../.github/workflows/ci.yml)) deckt den gesamten Zyklus ab:

```text
build ➔ unit-test ➔ integration-test ➔ system-test ➔ publish ➔ delivery & deploy
```

### Continuous Delivery (Automatische Release-Erstellung)
* **Wann:** Automatisch bei jedem Release-Tag nach SemVer (z. B. `v1.0.0`, `v1.1.0`).
* **Was passiert (`delivery`-Job):**
  1. Lädt die in `build` erzeugten, getesteten JAR-Dateien herunter.
  2. Erstellt via GitHub CLI (`gh release create`) ein offizielles **GitHub Release**.
  3. Hängt die JAR-Dateien als Release-Assets an.
  4. Generiert automatisch Release Notes und dokumentiert die zugehörigen Docker-Images in GHCR.

### Continuous Deployment (Automatische Auslieferung via Docker)
* **Wann:** Bei jedem Push auf `main`, bei Release-Tags (`v*`) oder bei manuellem Start (`workflow_dispatch`).
* **Was passiert (`deploy`-Job):**
  1. Ermittelt das exakte Deployment-Tag (`1.0.0`, `main` oder `sha-<commit>`).
  2. Meldet sich mit dem flüchtigen `GITHUB_TOKEN` bei der Registry `ghcr.io` an.
  3. Führt `docker compose -f docker-compose.deploy.yml pull` aus, um die versionierten Images zu laden.
  4. Startet die Container mit `docker compose up -d`.
  5. Führt sofort einen automatisierten **Smoke Test** aus ([`smoke-test.sh`](../Code/P2/deployment/smoke-test.sh)).
  6. Protokolliert den Nachweis der laufenden versionierten Container in der GitHub Actions Zusammenfassung (`$GITHUB_STEP_SUMMARY`).

---

## 3. Nachweis, welches versionierte Artefakt deployed wurde

Um jederzeit nachvollziehen zu können, welcher Code-Stand in der Umgebung aktiv ist, wird kein unklares `latest`-Tag verwendet, sondern ein eindeutiges Tag:

* **Image-Referenz:** `ghcr.io/<owner>/m324-flughafen-service:<tag>` (z. B. `main` oder `sha-1d11ac3`).
* **Runtime-Prüfung:** Der Deployment-Job und Administratoren auf dem Server prüfen das aktive Artefakt über Docker-Befehle:

```bash
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
```

Beispiel-Ausgabe der laufenden versionierten Artefakte:
```text
NAMES                     IMAGE                                                   STATUS         PORTS
m324-flug-service         ghcr.io/ellisabeta/m324-flug-service:main               Up 42 seconds  0.0.0.0:8082->8082/tcp
m324-flughafen-service    ghcr.io/ellisabeta/m324-flughafen-service:main          Up 43 seconds  0.0.0.0:8081->8081/tcp
m324-mongo                mongo:7.0                                               Up 44 seconds  0.0.0.0:27017->27017/tcp
```

Zusätzlich wird der kryptografische SHA256-Digest des Images verifiziert:
```bash
docker inspect --format '{{.Name}}: {{.Config.Image}} -> {{.Image}}' m324-flughafen-service m324-flug-service
```

---

## 4. Smoke Test nach Deployment Nachweis

Unmittelbar nach dem Start der Container führt die Pipeline das Skript [`Code/P2/deployment/smoke-test.sh`](../Code/P2/deployment/smoke-test.sh) aus:

1. **Readiness Probe:** Wartet in einer Schleife (max. 60s), bis beide Services per HTTP erreichbar sind.
2. **Health Check:** Prüft `GET /api/airports` auf Status `200` oder `404` (bei leerer DB).
3. **Flughafen-Erstellung:** Sendet `POST /api/airports` und prüft, ob `201 Created` (bzw. `409 Conflict`) zurückgegeben wird.
4. **Flug-Validierung:** Sendet einen ungültigen Flug-Request mit identischem Start-/Zielflughafen und verifiziert, dass die Geschäftslogik mit HTTP `400 Bad Request` reagiert.

Auszug aus dem Log des Smoke Tests in der Pipeline:
```text
=== Starte Smoke Test nach Deployment ===
Flughafen-Service: http://127.0.0.1:8081
Flug-Service:      http://127.0.0.1:8082
Warte auf Erreichbarkeit von Flughafen-Service (http://127.0.0.1:8081/api/airports)...
  -> Flughafen-Service ist erreichbar (Versuch 3)
Warte auf Erreichbarkeit von Flug-Service (http://127.0.0.1:8082/api/flights)...
  -> Flug-Service ist erreichbar (Versuch 2)

--- 1. Health & Read-Prüfung: /api/airports ---
OK: /api/airports antwortet mit HTTP 200

--- 2. Funktionstest: Flughafen ZRH anlegen ---
OK: Flughafen anlegen (Status 201)

--- 3. Funktionstest: Flugservice Validierung (Smoke-Check) ---
OK: Flugservice verarbeitet Request korrekt (HTTP 400 für gleiche Start/Ziel-Flughäfen)

=== Smoke Test erfolgreich abgeschlossen! ===
```

---

## 5. Health Check, Log-Auszug oder einfacher Monitoring-Nachweis

### Health & Readiness Check
Die Verfügbarkeit wird kontinuierlich bei jedem Deployment über curl-basierte Health Checks sichergestellt:
- Flughafen-Service: `curl -s -f http://localhost:8081/api/airports`
- Flug-Service: `curl -s -f http://localhost:8082/api/flights`

### Log-Auszug
Falls ein Service nicht startet oder unerwartet abbricht, greift der Schritt `docker compose logs` in GitHub Actions:
```text
m324-flughafen-service  | 2026-11-01T19:30:00.123Z  INFO 1 --- [main] c.t.f.FlughafenServiceApplication : Started FlughafenServiceApplication in 3.412 seconds (process running for 3.987)
m324-flug-service       | 2026-11-01T19:30:02.456Z  INFO 1 --- [main] c.t.f.FlugServiceApplication      : Started FlugServiceApplication in 3.821 seconds (process running for 4.215)
```

### Monitoring
Laufende Container können mit Docker-eigenen Ressourcenüberwachungs-Befehlen überwacht werden:
```bash
docker stats --no-stream m324-flughafen-service m324-flug-service m324-mongo
```

---

## 6. Rollback Strategie mit Simulation
Bei einem fehlerhaften Update setzen wir beide Services auf die zuletzt gesicherten, funktionierenden Docker-Images zurück. Dafür verwenden wir das Bash-Skript [`Code/P2/rollback/rollback.sh`](../Code/P2/rollback/rollback.sh).

### Ablauf des Rollbacks:
1. Funktionierenden Stand vor dem Update sichern:

```wsl
bash Code/P2/rollback/rollback.sh save
```

Erwartete Meldung: SAVE_OK. Die Image-IDs beider Services werden lokal gespeichert.

2. Gesicherten Stand anzeigen:

```wsl
bash Code/P2/rollback/rollback.sh status
```

3. Nach einem fehlerhaften Update den gesicherten Stand wiederherstellen:

```wsl
bash Code/P2/rollback/rollback.sh restore
```

Beide Anwendungscontainer werden mit den gesicherten Images gestartet. MongoDB bleibt unverändert. Nach erfolgreicher Image- und HTTP-Prüfung erscheint RESTORE_OK.

4. Containerzustand kontrollieren:

```wsl
docker compose -f Code/P2/docker-compose.deploy.yml -p p2 ps
```

Wichtig: Nach einem fehlerhaften Update nicht erneut save ausführen, da dies den bisherigen Checkpoint überschreiben könnte. Ohne Versionsänderung zwischen Sicherung und Wiederherstellung wird nur die Wiederherstellung derselben Version geprüft.

### Überprüfung durch Simulation:
- **Simulation starten:** ```bash Code/P2/rollback/simulate.sh``` ausführen.
- **Testumgebung:** Das echte Rollback-Skript wird ausgeführt. Docker-Befehle und HTTP-Antworten werden durch kontrollierte Testantworten ersetzt.
- **Fehlerfälle prüfen:** Fehlende Sicherungen, nicht verfügbare Images und ein teilweise fehlgeschlagener Rollback werden gezielt simuliert.
- **Wiederherstellung prüfen:** Nach einem simulierten Fehler wird geprüft, ob eine erneute Ausführung den gesicherten Stand erfolgreich wiederherstellt.
- **Laufende Anwendung schützen:** Die Simulation verändert keine echten Container oder Daten.

---

## 7. Offene Risiken
- **Datenbank-Migrationen:** Ein Rollback der Container setzt den Code zurück, modifiziert aber keine bereits veränderten Datenbankschemata.
- **Netzwerkunterbrüche:** Falls die Registry GHCR während des Deployments kurzzeitig nicht erreichbar ist, schlägt der Pull fehl; dank der Pipeline bricht der Prozess aber kontrolliert ab, bevor alte funktionierende Stände beschädigt werden.
