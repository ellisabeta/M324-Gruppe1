# Z1 – Kennzahlen / Continuous Improvement

Geschäftsreport für Lehrperson / Projektleitung. Stand: 02.11.2026, Datenbasis 20.09.2026 – 01.11.2026.

## Management Summary

| Bereich | Kennzahl | Wert | Bewertung |
|---|---|---|---|
| Delivery | Erfolgreiche Deployments | 2 von 2 | 🟢 Deployment funktioniert, aber erst seit 01.11. automatisiert |
| Delivery | Lead Time Pull Request (Median) | 1.6 h | 🟢 Änderungen kommen schnell auf `main` |
| Stabilität | Fehlerrate der aktuellen Pipeline (`ci.yml`) auf `main` | 0 % | 🟢 `main` ist mit der aktuellen Pipeline nie rot gewesen |
| Stabilität | Fehlerrate aller Workflows auf `main` | 50 % | 🔴 Altlast: ein generierter Workflow (`maven.yml`) schlug 6× fehl, ohne dass jemand reagierte |
| Pipeline | Erfolgsquote aller Runs | 72.5 % (29/40) | 🟡 8 von 11 Fehlern stammen vom selben toten Workflow |
| Pipeline | Dauer pro Run (Median / Max) | 1.1 / 4.6 min | 🟢 schnelles Feedback |
| Qualität | Anzahl automatisierter Tests | 19 (9 + 10) | 🟡 jeder Endpoint getestet, aber wenig Grenzfälle |
| Team | Commits pro Person | 24 / 20 / 13 / 8 | 🟡 Arbeit ungleich verteilt |

**Wichtigste Erkenntnis:** Unsere aktuelle CI/CD-Pipeline ist stabil und schnell. Die schlechten Gesamtwerte kommen von einem alten, automatisch erzeugten Workflow, der wochenlang fehlschlug, ohne dass jemand reagiert hat. Daraus leiten wir die Massnahmen unten ab.

## Wie die Kennzahlen erhoben werden

Alle Werte sind gemessen, nicht geschätzt. Das Skript [collect_metrics.py](../Code/P2/metrics/collect_metrics.py) liest:

| Quelle | Was | Wie |
|---|---|---|
| GitHub Actions API | Workflow-Runs, Jobs, Dauer, Status | `GET /repos/.../actions/runs` und `.../runs/{id}/jobs` |
| GitHub Pull Request API | Erstell- und Merge-Zeitpunkt | `GET /repos/.../pulls?state=closed` |
| Git-Historie | Commits, Autoren, Kalenderwochen, Fix-Commits | `git log` |
| Quellcode | Zeilen Code, Anzahl `@Test` | Dateien unter `Code/P2/*/src` |

```bash
python3 Code/P2/metrics/collect_metrics.py
```

Das Skript schreibt die Rohdaten nach `Code/P2/metrics/metrics.json` und die Tabellen nach [Z1-Kennzahlen-Daten.md](Z1-Kennzahlen-Daten.md). Ohne Token erlaubt GitHub 60 API-Anfragen pro Stunde; bei Bedarf `GITHUB_TOKEN` setzen.

## Die Kennzahlen im Detail

Wir orientieren uns an den vier **DORA-Kennzahlen** (DevOps Research and Assessment), weil sie direkt messen, ob CI/CD seinen Zweck erfüllt: oft, schnell und sicher ausliefern. Ergänzt werden sie mit Pipeline-, Qualitäts- und Teamkennzahlen.

### 1. Deployment Frequency – Wie oft wird ausgeliefert?

- **Definition:** Anzahl Runs, in denen der Job `Docker Deployment` in `ci.yml` gelaufen ist (nur Pushes auf `main` und Tags `v*`).
- **Warum für uns:** P4 verlangt automatisches Deployment. Die Zahl zeigt, ob es wirklich genutzt wird.
- **Wert:** 2 Deployments, beide erfolgreich (01.11.2026). Release-Tag `v1.0.0` (26.10.) lief noch ohne Deployment-Job.
- **Einschränkung:** Das Deployment läuft auf dem GitHub-Runner, nicht auf einem dauerhaften Server. Die Zahl misst also «auslieferbar und startbar», nicht «live bei Benutzern».

### 2. Lead Time for Changes – Wie lange dauert eine Änderung bis `main`?

- **Definition:** Zeit von «Pull Request erstellt» bis «Pull Request gemerged».
- **Warum für uns:** Wir arbeiten mit Feature-Branches und PRs. Lange offene PRs erhöhen das Risiko von Merge-Konflikten.
- **Wert:** Median 1.6 h, Maximum 181.9 h (≈ 7.5 Tage), 8 gemergte PRs.
- **Interpretation:** Die meisten PRs werden am gleichen Tag gemerged. Der Ausreisser ist ein Feature-Branch, der über eine Woche offen blieb.

### 3. Change Failure Rate – Wie oft geht `main` kaputt?

- **Definition:** Anteil fehlgeschlagener Workflow-Runs auf `main`.
- **Warum für uns:** `main` muss jederzeit deploybar sein, weil von dort deployed wird.
- **Wert:** Alle Workflows: 50 % (6 von 12). Nur aktuelle Pipeline `ci.yml`: 0 % (0 von 4).
- **Interpretation:** Alle 6 Fehler auf `main` stammen von `maven.yml`, einer GitHub-Vorlage, die nicht zu unserer Ordnerstruktur passte. Sie wurde erst am 18.10. entfernt.

### 4. Time to Restore – Wie schnell ist ein Fehler behoben?

- **Definition:** Zeit von einem fehlgeschlagenen Run bis zum nächsten erfolgreichen Run des gleichen Workflows auf dem gleichen Branch.
- **Warum für uns:** Zeigt, ob wir auf rote Pipelines reagieren.
- **Wert:** Keine messbaren Fälle. Kein Fehler wurde «repariert»: `maven.yml` wurde gelöscht statt korrigiert, `feature/P3-fehlerfall` war ein absichtlicher Fehlerfall für P3.
- **Interpretation:** Genau das ist der Befund: Fehler blieben bis zu 3 Wochen (28.09. – 18.10.) unbeachtet. Für den Ernstfall gibt es das [Rollback-Skript](P4-CD.md#rollback-strategie-mit-simulation).

### 5. Pipeline-Erfolgsquote und -Dauer

- **Definition:** Anteil erfolgreicher Runs; Zeit von Start bis Ende eines Runs.
- **Warum für uns:** Eine langsame oder unzuverlässige Pipeline wird umgangen. Ziel aus P3: Feedback in unter 10 Minuten.
- **Wert:** 72.5 % Erfolg (29/40). Dauer Median 1.1 min, Maximum 4.6 min.
- **Fehler pro Workflow:** `maven.yml` 8, `ci.yml` 2 (absichtlicher Fehlerfall), `ci-variant-c.yml` 1.

### 6. Testabdeckung (vereinfachte Messung)

- **Definition:** Anzahl `@Test`-Methoden und Verhältnis Test-Codezeilen zu Produktiv-Codezeilen.
- **Warum für uns:** Ohne Tests ist automatisches Deployment gefährlich. Echte Line-Coverage (JaCoCo) haben wir noch nicht eingerichtet.

| Service | Produktiv-LOC | Test-LOC | Test/Code | Tests |
|---|---|---|---|---|
| flughafen-service | 281 | 166 | 0.59 | 9 |
| flug-service | 334 | 265 | 0.79 | 10 |

### 7. Team- und Prozesskennzahlen

- **Commits pro Person:** GabrielArocha7 24, Tahssin 20 (inkl. Identität `TahssinAl`), g1uLiax 13, elliisabeta 7 (+ 1 Commit `Elili`).
- **Commits pro Kalenderwoche:** W34: 16, W35: 8, W36: 5, W37: 1, W38: 12, W39: 11, W40: 2, W42: 4, W44: 6 (W41 und W43: keine).
- **Anteil Fix-Commits:** 4.6 % (3 von 65).
- **Warum für uns:** Zeigt Arbeitsverteilung und Rhythmus. Commits messen **nicht** die Leistung einer Person (Grösse und Schwierigkeit sind sehr unterschiedlich, Pair Programming erscheint nur bei einer Person). Wir verwenden die Zahl nur, um Ungleichgewichte im Team anzusprechen.
- **Datenqualität:** Manche Personen committen mit mehreren Git-Identitäten. Vor der Bewertung zusammenführen (z. B. mit einer `.mailmap`).

## Bewusst nicht erhobene Kennzahlen

| Kennzahl | Grund |
|---|---|
| Umfragen zur Applikation | Keine echten Benutzer. Eine Umfrage im eigenen Team wäre nicht aussagekräftig. |
| Anzahl Tickets pro Tag / Feature | Wir haben kein Ticket-System mit Fehlermeldungen (siehe Z2). GitHub Issues werden nicht genutzt. |
| Geschätzter vs. tatsächlicher Aufwand | Daten liegen im Zeitlogging (Teams-Excel), aber ohne Schätzung pro Task. Kann ab jetzt erfasst werden (siehe Massnahmen). |
| Antwortzeiten / Fehlerrate im Betrieb | Kein dauerhafter Betrieb. Gehört zu Monitoring (Z3). |
| Line-Coverage | JaCoCo ist nicht eingerichtet. Ersetzt durch Testanzahl und Test/Code-Verhältnis. |

## Massnahmen (Continuous Improvement)

| Befund | Massnahme | Messung der Wirkung |
|---|---|---|
| Rote Workflows blieben wochenlang unbeachtet | Branch Protection auf `main`: Merge nur mit grüner `ci.yml`. Fehlschlag wird im nächsten Teamtreffen angesprochen. | Time to Restore < 1 Tag |
| Ungenutzte Workflows verfälschen die Statistik | Workflow-Varianten aus P3 (`ci-variant-*.yml`) archivieren, nur `ci.yml` aktiv lassen | Erfolgsquote aller Runs = Erfolgsquote `ci.yml` |
| Ein PR blieb 7.5 Tage offen | PRs spätestens am nächsten Arbeitstag reviewen | Lead Time Maximum < 48 h |
| Testabdeckung nur grob messbar | JaCoCo in beide `pom.xml` aufnehmen | Line-Coverage pro Service im CI-Report |
| Aufwand pro Task unbekannt | Im Zeitlogging pro Task eine Schätzung ergänzen | Abweichung geschätzt vs. tatsächlich |
| Ungleiche Verteilung der Commits | Aufgaben im Planning bewusst rotieren | Commits und PRs pro Person |

## KI-Nachweis

KI (Claude) wurde verwendet, um Kennzahlen vorzuschlagen, das Erhebungsskript zu schreiben und den Report zu strukturieren. Alle Werte stammen aus dem Skript, also aus GitHub-API und Git. Kein Wert wurde von der KI geschätzt oder ergänzt.

| KI-Vorschlag | Entscheid | Begründung / Prüfung |
|---|---|---|
| Vier DORA-Kennzahlen als Kern | übernommen | Passen direkt zu P3/P4. Werte im GitHub-Actions-Tab stichprobenartig nachgeprüft. |
| Change Failure Rate über alle Workflows | angepasst | Ergab 50 %. Bei der Prüfung der einzelnen Runs fiel auf, dass alle Fehler von `maven.yml` kommen. Darum zusätzlich nur `ci.yml` ausgewiesen. |
| Time to Restore pro Workflow-Name | angepasst | Unser Workflow wurde von «CI» zu «CI / CD» umbenannt. Jetzt wird über den Dateipfad gruppiert. |
| Copilot-Review-Runs mitzählen | verworfen | Sind keine Pipeline von uns (`event: dynamic`), werden herausgefiltert. |
| Commits pro Person als Leistungskennzahl | angepasst | Nur als Hinweis auf Verteilung, nicht als Leistung. Mehrfach-Identitäten von Hand geprüft. |
| Kundenumfrage / Ticket-Kennzahlen | verworfen | Keine echten Benutzer und kein Ticket-System. Werte wären erfunden. |
| Line-Coverage mit JaCoCo | verworfen (vorerst) | Nicht eingerichtet. Als Massnahme aufgenommen. |
| Antwortzeiten der Services | verworfen | Kein dauerhafter Betrieb, gehört zu Z3. |
