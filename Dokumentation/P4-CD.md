# P4 – Continuous Deployment

## Gewählte Deployment Variante
Wir verwenden **Docker Compose**, um den Flughafenservice, den Flugservice und MongoDB gemeinsam als Container zu starten.
- **Docker Desktop** stellt dafür auf unseren lokalen Rechnern die **Docker-Umgebung** bereit.
- Die **Services, Ports** und **Datenbankverbindungen** sind in der bestehenden [docker-compose.yml](../Code/P2/docker-compose.yml) definiert.

Diese Variante passt zu unserem Projekt, da beide Java-Services bereits eigene Dockerfiles besitzen. Compose ermöglicht einen einfachen, wiederholbaren Start der gesamten Anwendung mit einem Befehl. Für unser kleines Projekt ist dafür keine zusätzliche Plattform wie Kubernetes notwendig.

## Verwendete Umgebung

## Smoke Test nach Deployment Nachweis

## Health Check, Log-Auszug oder einfacher Monitoring-Nachweis

## Rollback Strategie mit Simulation
Bei einem fehlerhaften Update setzen wir beide Services auf die zuletzt gesicherten, funktionierenden Docker-Images zurück. Dafür verwenden wir das Bash-Skript ``Code/P2/rollback/rollback.sh``

### Ablauf des Rollbacks:

- Vor dem Update: ``rollback.sh save`` ausführen. Das Skript prüft die laufenden Services und speichert ihre Image-IDs.

- Bei einem Fehler: Den Rollback manuell mit ``rollback.sh restore`` starten.

- Wiederherstellung: Beide Anwendungscontainer werden mit den gesicherten Images gestartet. MongoDB bleibt unverändert.

- Überprüfung: Das Skript kontrolliert die Image-IDs und HTTP-Antworten der Services.

- Erfolgsmeldung: Erst wenn alle Prüfungen erfolgreich sind, wird der Rollback als erfolgreich gemeldet.

### Überprüfung durch Simulation:

- Simulation starten: ``simulate.sh`` ausführen.

- Testumgebung: Das echte Rollback-Skript wird ausgeführt. Docker-Befehle und HTTP-Antworten werden durch kontrollierte Testantworten ersetzt.

- Fehlerfälle prüfen: Fehlende Sicherungen, nicht verfügbare Images und ein teilweise fehlgeschlagener Rollback werden gezielt simuliert.

- Wiederherstellung prüfen: Nach einem simulierten Fehler wird geprüft, ob eine erneute Ausführung den gesicherten Stand erfolgreich wiederherstellt.

- Laufende Anwendung schützen: Die Simulation verändert keine echten Container oder Daten

## Offene Risiken
