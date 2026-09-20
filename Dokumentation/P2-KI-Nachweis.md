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
