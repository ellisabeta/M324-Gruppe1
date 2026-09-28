# P2 – KI-Nachweis

Ich habe KI nur als Hilfe bei Problemen verwendet. Die Vorschläge habe ich selber geprüft.

## Java-Fehler

Beim Befehl `java -version` kam:

```text
Bad CPU type in executable
```

Die KI erklärte, dass mein Mac Apple Silicon verwendet, aber ein Intel-JDK gestartet wurde. Ich habe das mit `uname -m` geprüft und danach ein passendes ARM-JDK ausgewählt:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17 -a arm64)
export PATH="$JAVA_HOME/bin:$PATH"
```

Danach funktionierten Java 17 und Maven. Das habe ich mit `java -version` und `mvn -version` geprüft.

## MongoDB-Atlas-Fehler

Beim Aufruf von `/api/airports` kam zuerst `500 Internal Server Error`.

Die KI schlug vor, die Logs, die `.env` und die IP-Freigabe in MongoDB Atlas zu prüfen. In den Logs stand ein `MongoTimeoutException` mit einem SSL-Fehler.

Ich habe danach meine aktuelle IP-Adresse in Atlas freigegeben und die `.env` vor dem Start geladen. Danach kam:

```text
404 Not Found – Es sind noch keine Flughäfen vorhanden.
```

Das war richtig, weil die Datenbank noch leer war. Damit konnte ich die Verbindung zu Atlas selber bestätigen.

## Flugservice

Für den Flugservice habe ich KI für Ideen zur Aufteilung, Validierung und für Testfälle verwendet.

Übernommen und angepasst habe ich:

- Prüfung von Start- und Landezeit
- Prüfung, dass Start- und Zielflughafen verschieden sind
- Prüfung des Flugzeugtyps
- Aufruf des Flughafenservices über einen eigenen Client

Ich habe die Vorschläge an mein Projekt angepasst und mit den Unit-Tests geprüft. Dabei wurden erfolgreiche und fehlerhafte Eingaben getestet. Die Tests liefen mit `mvn test` erfolgreich durch.

Die endgültige Entscheidung, welche Vorschläge verwendet werden, habe ich selber getroffen. Passwörter und die `.env` wurden nicht in den Code oder in Git übernommen.
