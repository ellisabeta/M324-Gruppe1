# P3 – Continuous Integration

> Stellen mit ✏️ müssen nach den eigenen Pipeline-Läufen ausgefüllt bzw. selbst formuliert werden.

## 1. Technologie: GitHub Actions

Unser Code liegt auf GitHub, deshalb verwenden wir GitHub Actions.

| Begriff | Bedeutung | Bei uns |
|---|---|---|
| Workflow | YAML-Datei in `.github/workflows/`, wird durch ein Event ausgelöst | `ci.yml`, `ci-variant-*.yml` |
| Event / Trigger | Auslöser, z. B. `push`, `pull_request`, `workflow_dispatch` | siehe Kapitel 2 |
| Job | Läuft auf einem eigenen, frischen Runner. Jobs laufen parallel, ausser sie sind mit `needs` verkettet | `build`, `unit-test`, `integration-test`, `system-test` |
| Step | Einzelner Befehl (`run`) oder eine fertige Action (`uses`) innerhalb eines Jobs | `mvn verify`, `actions/checkout` |
| Runner | VM von GitHub, auf der ein Job läuft | `ubuntu-24.04` |
| Matrix | Startet denselben Job mehrfach mit unterschiedlichen Werten | `service: [flughafen-service, flug-service]` |
| Artefakt | Datei, die ein Job hochlädt, damit andere Jobs oder Personen sie nutzen können | JARs, Testreports |
| Cache | Wiederverwendung von Dateien zwischen Runs | `~/.m2` (Maven-Abhängigkeiten) |
| Reusable Workflow | Workflow mit `workflow_call`, der von anderen Workflows aufgerufen wird | `_system-test.yml`, `_maven-service.yml` |

Da jeder Job auf einem eigenen Runner startet, muss jeder Job den Code neu auschecken und Java einrichten. Ergebnisse (z. B. das JAR) werden über Artefakte weitergegeben.

## 2. Branching-Strategie und Trigger

Die Branching-Strategie aus P2 ([P2-Prozess.md](P2-Prozess.md)) bleibt: kurzlebige `feature/*`-Branches, Pull Request auf `main`, Review, Squash-Merge. Die CI ist daran so gekoppelt:

| Event | Branch | Workflow | Zweck |
|---|---|---|---|
| `push` | `feature/**` (nur bei Änderungen in `Code/P2/**`) | `ci.yml` | Schnelles Feedback während der Entwicklung |
| `pull_request` | Ziel `main` | `ci.yml` (ohne paths-Filter) | Qualitäts-Gate vor dem Merge |
| `workflow_dispatch` | beliebig | alle | Manueller Start, z. B. durch die Lehrperson |
| `push` | `feature/P3-**` | `ci-variant-*.yml` | Nur zum Vergleichen der Varianten |

Regeln für `main` (Branch Protection):

- Merge nur über Pull Request mit mindestens einem Review.
- Required Status Checks: alle Jobs von `CI` müssen grün sein.
- Keine direkten Pushes auf `main`.

✏️ Screenshot der Branch-Protection-Regel einfügen.

Warum kein paths-Filter beim `pull_request`? Ein „required“ Check, der wegen eines Filters nicht startet, blockiert den PR für immer. Beim `push` auf Feature-Branches spart der Filter dagegen unnötige Läufe (z. B. bei reinen Doku-Änderungen).

Die Lehrperson muss Collaborator im Repository `ellisabeta/M324-Gruppe1` sein (✏️ prüfen). Dann kann sie im Tab „Actions“ jeden Workflow über „Run workflow“ starten.

## 3. Teststufen

| Stufe | Was wird getestet | Werkzeug | Dateien | Maven |
|---|---|---|---|---|
| Unit | Controller isoliert, Service gemockt | JUnit 5, MockMvc, Mockito | `*ControllerTest.java` | `mvn test` (Surefire) |
| Integration | Ganzer Service (HTTP → Controller → Service → Repository) mit echter MongoDB | Spring Boot Test, Testcontainers | `*ApiIT.java` | `mvn verify -DskipUnitTests` (Failsafe) |
| System | Beide Services + MongoDB als Container, echte HTTP-Aufrufe, auch Ausfall des Flughafenservices | docker compose, curl | `Code/P2/system-test/system-test.sh` | – |

Integrationstests:

- `AirportApiIT`: Flughafen wird gespeichert und über GET wieder geliefert; doppeltes Kürzel → `409` gegen die echte Datenbank; leere Datenbank → `404`.
- `FlightApiIT`: gültiger Flug wird in MongoDB gespeichert; unbekannter Flughafen → `404` und nichts gespeichert; Flughafenservice nicht erreichbar → `502`. Der `AirportClient` ist hier gemockt, die Kommunikation zwischen den Services prüft der Systemtest.

Systemtest (11 Prüfungen): leere DB → 404, Flughäfen erstellen → 201, Duplikat → 409, ungültiges Kürzel → 400, GET → 200, gültiger Flug → 201, unbekannter Flughafen → 404, gleicher Flughafen → 400, Landung vor Start → 400, Flughafenservice gestoppt → 502.

Lokal ausführen:

```bash
cd Code/P2/flug-service && mvn verify          # Unit + Integration (Docker nötig)
cd Code/P2 && docker compose up -d --build && bash system-test/system-test.sh
```

## 4. Varianten

| | A – Monolith | B – Stages als Jobs | C – Matrix | D – Workflow pro Service | Final (`ci.yml`) |
|---|---|---|---|---|---|
| Datei | `ci-variant-a.yml` | `ci-variant-b.yml` | `ci-variant-c.yml` | `ci-variant-d-*.yml` + `_maven-service.yml` | `ci.yml` |
| Jobs | 1 | 4 (nacheinander) | 2 parallel + Systemtest | 1 pro Service | 2 × 3 parallel + Systemtest |
| Services | nacheinander | nacheinander im Job | parallel | getrennte Workflows | parallel |
| Maven-Cache | nein | ja | ja | ja | ja |
| Systemtest | ja | ja | ja | nein | ja |
| Laufzeit Run 1 ✏️ | | | | | |
| Laufzeit Run 2 ✏️ | | | | | |
| Laufzeit Run 3 ✏️ | | | | | |
| Link zum Run ✏️ | | | | | |

Messwert vor den Erweiterungen (nur flug-service, nur Unit Tests, ohne Cache): [Run 36343278338](https://github.com/ellisabeta/M324-Gruppe1/actions/runs/36343278338) – Job 36 s, davon Maven 24 s, 4 Tests grün.

Gemessen wird die Gesamtdauer des Runs („Total duration“ im Actions-Tab). Run 1 ist ein „kalter“ Lauf ohne Cache, ab Run 2 greift der Maven-Cache.

### Beobachtungen pro Variante ✏️

- **A:** Einfach zu lesen. Schlägt ein Schritt fehl, werden die folgenden nicht mehr ausgeführt. Im Log muss man suchen, welche Stufe fehlgeschlagen ist.
- **B:** Im Actions-Tab sieht man sofort, welche Stufe rot ist. Jeder Job startet einen neuen Runner, dadurch mehr Overhead (Checkout, Java, Cache laden).
- **C:** Beide Services laufen gleichzeitig, `fail-fast: false` zeigt Fehler von beiden Services. Die Stufen sind aber wieder in einem `mvn verify` zusammengefasst.
- **D:** Nur der geänderte Service wird gebaut. Kein Systemtest möglich, weil kein Workflow beide Services kennt. Die Logik ist dank Reusable Workflow nicht doppelt.

### Fehlerfall ✏️

Einen Test absichtlich fehlschlagen lassen (z. B. erwarteten Status in `AirportControllerTest` ändern), pushen und festhalten:

- Welcher Job wird rot, welche Jobs werden übersprungen?
- Wo im Log steht der Fehler? (Log-Auszug einfügen)
- Sind die Testreports trotzdem als Artefakt vorhanden?

## 5. Finale Variante ✏️

`ci.yml` kombiniert B und C: Jede Teststufe ist ein eigener Job, und jeder Job läuft per Matrix parallel für beide Services.

Begründung (Entwurf, mit euren Messwerten prüfen und selbst formulieren):

- Fehler sind direkt der Stufe und dem Service zugeordnet.
- Günstige Tests laufen zuerst (Fail Fast): Unit Tests vor Integrationstests vor Systemtest.
- Services laufen parallel, dadurch kürzere Laufzeit als B.
- Der Build-Job liefert pro Service ein klar benanntes JAR, an das P3b anknüpft.
- Nachteil: mehr Runner-Overhead als A, weil jeder Job neu aufsetzt. Der Maven-Cache reduziert das.

## 6. Effizienz und Stabilität

- **Caching:** `actions/setup-java` mit `cache: maven`, Schlüssel über beide `pom.xml`. ✏️ Laufzeit A (ohne Cache) vs. final (mit Cache) vergleichen.
- **Parallelisierung:** Matrix über die Services.
- **Abbruch alter Läufe:** `concurrency` mit `cancel-in-progress` – ein neuer Push bricht den alten Lauf auf demselben Branch ab.
- **Timeouts:** jeder Job hat `timeout-minutes`, damit ein hängender Test nicht 6 h läuft.
- **Fixe Runner-Version:** `ubuntu-24.04` statt `ubuntu-latest`, damit sich die Umgebung nicht unbemerkt ändert.
- **Fehleranalyse:** Testreports werden mit `if: always()` auch bei Fehlern hochgeladen; beim Systemtest werden bei Fehlern die Container-Logs ausgegeben.
- **Minimale Rechte:** `permissions: contents: read`.
- **Keine Secrets:** Die CI nutzt eine MongoDB im Container, nicht die Atlas-Datenbank.

## 7. Vorbereitung P3b

Der `build`-Job in `ci.yml` erzeugt pro Service das Artefakt `<service>-<commit-sha>` mit dem ausführbaren JAR (`<service>-0.0.1-SNAPSHOT.jar`). In P3b wird ein Job `publish` mit `needs: system-test` ergänzt, der dieses JAR mit einer Version versieht und in ein Artefakt-Repository publiziert. Die Dockerfiles in beiden Services können dort auch für ein Container-Image verwendet werden.

## 8. KI-Nachweis

### Review 1: veraltete Actions und Runner-Label

**Ausgangslage:** Der erste Lauf von Variante A ([Run 36343278338](https://github.com/ellisabeta/M324-Gruppe1/actions/runs/36343278338)) war grün, zeigte aber Warnungen:

```text
! Node.js 20 is deprecated. The following actions target Node.js 20 but are being forced to run on Node.js 24: actions/checkout@v4, actions/setup-java@v4, actions/upload-artifact@v4.
! setup-java v4 is deprecated and will no longer receive updates. Please migrate to actions/setup-java@v5.
- The ubuntu-latest label will migrate to Ubuntu 26 beginning October 19, 2026.
```

**Prompt:** „Hier sind die Warnungen aus unserem Pipeline-Lauf. Was bedeuten sie und was sollen wir ändern?“

**KI-Hinweis:** Die Actions auf die aktuellen Major-Versionen heben (die Release Notes wurden auf Breaking Changes geprüft) und statt `ubuntu-latest` eine feste Runner-Version verwenden, damit ein Wechsel auf Ubuntu 26 die Pipeline nicht unbemerkt verändert.

**Geänderte Stelle:**

```yaml
# vorher
runs-on: ubuntu-latest
- uses: actions/checkout@v4
- uses: actions/setup-java@v4
- uses: actions/upload-artifact@v4
# nachher
runs-on: ubuntu-24.04
- uses: actions/checkout@v7
- uses: actions/setup-java@v6
- uses: actions/upload-artifact@v7
```

**Validierung ✏️:** Link zum nächsten Lauf und prüfen, ob die Warnungen verschwunden sind.

**Eigene Schlussfolgerung ✏️:**

### Review 2: Testcontainers und Docker-Version

**KI-Hinweis:** Spring Boot 3.3.5 bringt Testcontainers 1.19.8 mit. Die aktuelle Docker Engine auf den GitHub-Runnern akzeptiert die alte Docker-API-Version dieser Testcontainers-Version nicht mehr; Testcontainers 1.21.4 behebt das („makes version 1.21.x works with recent Docker Engine changes“).

**Geänderte Stelle:** `pom.xml` beider Services: `<testcontainers.version>1.21.4</testcontainers.version>`

**Validierung ✏️:** Log-Auszug des Integrationstest-Jobs einfügen (Zeile mit `Tests run: …` aus Failsafe und der gestartete Container `mongo:7.0`).

**Eigene Schlussfolgerung ✏️:**

### Grundsätze

- Alle KI-Vorschläge wurden erst übernommen, nachdem die Pipeline damit auf GitHub gelaufen ist.
- Es wurden keine Secrets, Tokens oder Connection Strings (Atlas) in KI-Tools eingegeben.
