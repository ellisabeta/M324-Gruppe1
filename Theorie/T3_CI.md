# Continuous Integration

## Was ist Continuos Integration und wie wird es eingesetzt?
### Nutzung im Softwareentwicklungsprozess
Continuous Integration beschreibt in der agilen Softwareentwicklung den Prozess, Code-Änderung von Entwickler sehr häufig in ein zentrales Repository einzuchecken. Jede Änderung soll automatisch Tests laufen lassen, um Bugs früh zu erkennen.

CI löst ein bekanntes Problem der Softwareentwicklung: Wenn mehrere Entwickler isoliert an eigenen Ständen arbeiten und ihren Code erst am Ende zusammenführen, entsteht die sogenannte „Integration Hell".  Widersprüchliche Änderungen, Abhängigkeiten und Bugs, die sich kaum noch einem bestimmten Commit zuordnen lassen. Je länger ein Branch unabhängig existiert, desto grösser wird dieses Problem.

CI begegnet dem, indem die Integration selbst zu einem kontinuierlichen, automatisierten Vorgang gemacht wird, statt sie auf einen einzigen, Zeitpunkt am Ende zu verschieben.

### Technische Prozesse für eine erfolgreiche Umsetzung

Für eine funktionierende CI-Umsetzung müssen mehrere technische Bausteine vorhanden sein:

- **Zentrales Code-Repository / Versionskontrollsystem** (z. B. Git), in das alle Entwickler ihre Änderungen einchecken.
- **Build-Server** (z. B. GitLab CI/CD), der bei jedem Commit automatisch einen Build anstosst.
- **Automatisierte Test-Suite**, die bei jedem Build ausgeführt wird und Fehler unmittelbar zurückmeldet.
- **Ein zentrales Build-Tool**, das den Quellcode reproduzierbar zu einem lauffähigen Artefakt zusammensetzt.

Der typische Ablauf ist: Ein Entwickler committet Code, der CI-Server erkennt die Änderung, ein automatischer Build wird gestartet, automatisierte Tests laufen, bei Erfolg gilt der Code als integrationsfähig, bei Fehlschlag wird das Team sofort benachrichtigt, damit der Fehler behoben werden kann, bevor er sich weiter im System ausbreitet.

### Rolle von CI in Automatisierung und Teamzusammenarbeit

CI ist eine der zentralen Best Practices von DevOps.

Neben der reinen Technik hat CI auch eine kulturelle Komponente: Teams müssen bereit sein, häufig zu integrieren, gebrochene Builds sofort zu beheben und Verantwortung für die Stabilität der gemeinsamen Codebasis zu übernehmen. CI fördert dadurch Kommunikation und Feedback im Team, da Probleme sofort sichtbar werden und nicht erst nach Wochen auffallen. Zudem ermöglicht CI es, dass einzelne Aufgaben unabhängig und parallel von verschiedenen Entwicklern bearbeitet werden können, weil die häufige Integration Konflikte klein und beherrschbar hält.

**Quellen:**
- Mindsquare AG – Continuous Integration in der Software-Entwicklung: https://mindsquare.de/knowhow/continuous-integration/
- Computer Weekly – Was ist Kontinuierliche Integration (CI)?: https://www.computerweekly.com/de/definition/Kontinuierliche-Integration-Continuous-Integration
- Atlassian – Was ist Continuous Integration: https://www.atlassian.com/de/continuous-delivery/continuous-integration
- Wikipedia – Kontinuierliche Integration: https://de.wikipedia.org/wiki/Kontinuierliche_Integration
- AWS – Was ist CI?: https://aws.amazon.com/de/devops/continuous-integration/
- CircleCI – What is continuous integration (CI)?: https://circleci.com/continuous-integration/

## 2. Was sind Vor- und Nachteile von CI?

### Vorteile

CI bringt sowohl dem Entwicklerteam als auch dem gesamten Unternehmen Vorteile:

- **Frühe Fehlererkennung:** Da jeder Commit sofort gebaut und getestet wird, werden Integrationsfehler entdeckt, während der Kontext für den Entwickler noch frisch ist, somit ist die Fehlerbehebung deutlich günstiger als eine späte Entdeckung.
- **Höhere Softwarequalität:** Automatisierte Tests bei jedem Build stellen sicher, dass neue Änderungen die bestehende Codebasis nicht beschädigen.
- **Schnellere, stabilere Releases:** Weil der Hauptzweig ständig in einem lauffähigen Zustand gehalten wird, sind Releases jederzeit möglich und weniger riskant.
- **Bessere Skalierbarkeit:** CI erlaubt es, Teamgrössen und Codebasen zu vergrössern, ohne dass die Integration unübersichtlich wird.
- **Verbesserte Zusammenarbeit und Transparenz:** Ständiges Feedback zum Zustand der Software fördert Kommunikation im Team und schafft für das gesamte Unternehmen mehr Einblick in den Entwicklungsstand.
- **Geringeres Risiko und geringere Kosten:** Durch die Automatisierung von Tätigkeiten sinken Zeitaufwand und Fehlerkosten insgesamt.

### Herausforderungen

- **Initiale Kosten und Infrastruktur:** Der Aufbau einer geeigneten technischen Infrastruktur (Build-Server, Testautomatisierung) verursacht anfänglichen Aufwand und Kosten.
- **Einarbeitung eingespielter Teams:** Teams, die bislang ohne CI gearbeitet haben, müssen sich in neue Werkzeuge und Abläufe einarbeiten; die nachträgliche Einführung von CI in ein bestehendes, nicht CI-basiertes Projekt ist besonders anspruchsvoll.
- **Testaufwand:** Der Nutzen von CI hängt vollständig von der Qualität und dem Umfang der automatisierten Tests ab. Ohne Disziplin beim Schreiben und Pflegen der Tests nützt die Investition nichts, da fehlerhafte Builds trotzdem durchgelassen werden könnten.

### Langfristiger Einfluss auf Produktqualität und Workflow

Langfristig sorgt CI für eine dokumentierte, nachvollziehbare Historie durch Versionskontrolle, vereinfacht die Fehlerbehebung durch automatisierte Verfahren und beschleunigt den gesamten Entwicklungsworkflow. Da CI in der Praxis meist mit Continuous Delivery/Deployment kombiniert wird, führt die Umsetzung zu häufigeren Releases.

**Quellen:**
- Atlassian – Was ist Continuous Integration: https://www.atlassian.com/de/continuous-delivery/continuous-integration
- Mindsquare AG – Continuous Integration in der Software-Entwicklung: https://mindsquare.de/knowhow/continuous-integration/
- exwe – Was ist Continuous Integration? Vorteile, Herausforderungen: https://www.exwe.de/en/news/software-development/what-is-continuous-integration-advantages-challenges-and-tools-at-a-glance/
- IONOS Digital Guide – Continuous Integration: Definition, Vor- und Nachteile: https://www.ionos.com/digitalguide/websites/web-development/continuous-integration/

---

## 3. Was ist Continuous Testing und wie wird es umgesetzt?

### Unterschied zu anderen Testmethoden

Testen erfolgt meist sequentiell und zeitlich begrenzt: Entwickler schreiben Code, gaben ihn an eine separate QA-Abteilung weiter, diese testet am Ende der Entwicklung, meldete Probleme zurück, und erst dann werden Korrekturen vorgenommen. Dieser Kreislauf kann sich über Wochen oder Monate hinziehen.

Continuous Testing durchbricht dieses Muster: Automatisierte Tests laufen während des gesamten Software-Entwicklungszyklus, nicht als abgetrennte Aktivität am Ende, sondern eingebettet in jede Phase der CI/CD-Pipeline – von der Code-Integration bis zur Produktionsbereitstellung. Jede Änderung löst einen Test aus.

### Rolle im Entwicklungszyklus

Das vorrangige Ziel von Continuous Testing ist es nicht nur, einzelne Fehler zu finden, sondern die Abdeckung des Geschäftsrisikos zu bewerten: Kontinuierliche Tests liefern Erkenntnisse darüber, ob ein Release zu riskant ist oder freigegeben werden kann. Damit hängt Continuous Testing eng mit CI zusammen. Es ist die Testkomponente, die CI erst wirksam macht, denn ohne verlässliche automatisierte Tests liefert ein CI-Build lediglich Informationen darüber, ob der Code kompiliert und nicht ob er korrekt funktioniert.

### Arten automatisierter Tests 

Typischerweise werden in CI/CD-Pipelines mehrere Testarten automatisiert und kombiniert:

- **Unit-Tests:** Prüfen einzelne Codebausteine isoliert. Vorteil: einfach zu schreiben, sehr schnelle Ausführung, bilden die Architektur der Codebasis eng ab. Nachteil: Sie bilden keine echten Nutzer-Workflows ab, da diese meist mehrere Komponenten gleichzeitig betreffen.
- **API-Tests:** Prüfen die Schnittstellen zwischen Modulen, indem Aufrufe zwischen ihnen simuliert werden. Vorteil: im Allgemeinen einfach zu schreiben und schnell auszuführen.
- **Integrations-/End-to-End-Tests:** Bilden Nutzeraktionen nach und testen das Zusammenspiel mehrerer Komponenten. Vorteil: Bugs werden zuverlässiger gefunden, da echte Interoperabilität geprüft wird. Nachteil: langsamer als Unit-Tests, teils anfällig für Netzwerklatenzen oder Flakiness.

**Quellen:**
- Tricentis – Was ist Continuous Testing?: https://www.tricentis.com/de/learn/was-ist-continuous-testing
- Trend Report – Warum Continuous Testing so wichtig ist: https://trendreport.de/warum-continuous-testing-so-wichtig-ist/
- Garn Consulting – Continuous Testing in CI/CD-Pipelines: https://www.garn-consulting.de/2025/10/27/continuous-testing-in-ci-cd-pipelines-so-gelingt-die-umsetzung/
- Prescient Security – Continuous Testing: What is it?: https://prescientsecurity.com/resources/blogs/continuous-testing-what-is-it
- Atlassian – Was ist Continuous Integration (Testarten): https://www.atlassian.com/de/agile/software-development/continuous-integration

## 4. Was ist eine Branching-Strategie, und welches sind die bekanntesten Ansätze?

### Bedeutung für die Versionskontrolle
Eine Branching-Strategie legt fest, wie ein Team Branches in einem Versionskontrollsystem anlegt, benennt, integriert und wieder löscht, um Änderungen kontrolliert in ein Produkt zu überführen. Sie regelt insbesondere, wie Feature-, Release- und Hotfix-Branches entstehen, wann und wie sie zusammengeführt werden und welche Qualitätssicherung vorher greift. Eine klare Strategie sorgt dafür, dass alle Teammitglieder nach demselben Prozess arbeiten, ohne sie drohen Merge-Konflikte, verzögerte Releases und Verwirrung darüber, welcher Branch der „aktuelle" ist.

### Einfluss auf Code-Organisation und Arbeitsfluss
Die Wahl der Branching-Strategie hat direkten Einfluss darauf, wie parallel im Team gearbeitet werden kann, wie lange Änderungen isoliert bleiben, bevor sie integriert werden, und wie leicht sich Releases planen lassen. 

### Verbreitete Branching-Strategien im Vergleich
**Git Flow**
Von Vincent Driessen 2010 eingeführt, nutzt Git Flow zwei dauerhafte Branches – `main` (Produktion) und `develop` (Integration) – ergänzt durch temporäre Branches (`feature/*`, `release/*`, `hotfix/*`). Dieses Modell eignet sich besonders für Projekte mit geplanten, zeitlich getakteten Releases und langen Support-Zyklen, ist aber vergleichsweise komplex und bei sehr häufigen Deployments eher hinderlich. Driessen selbst merkte 2020 an, dass GitHub Flow für viele moderne, webbasierte Softwareprojekte heute oft der bessere Ausgangspunkt sei.

**GitHub Flow**
Eine deutlich schlankere Alternative: Es gibt nur den dauerhaften `main`-Branch, der stets produktionsreif sein soll, sowie kurzlebige Feature-Branches, die per Pull Request nach Review in `main` gemergt werden. GitHub Flow eignet sich gut für kleine Teams (2–10 Entwickler) und kontinuierliche Bereitstellung, verzichtet aber auf die Struktur von Git Flow für geplante Release-Zyklen.

**Trunk-Based Development (TBD) – vertiefter Vergleich**
Beim trunk-basierten Ansatz committen alle Entwickler direkt in den Hauptzweig (Trunk/`main`) oder verwenden nur sehr kurzlebige Feature-Branches (typischerweise unter 24 Stunden Lebensdauer). Größere Features, die sich nicht in einem Tag fertigstellen lassen, werden mittels **Feature Flags** oder **Branch by Abstraction** unsichtbar gemacht, statt sie in einem separaten, langlebigen Branch zu entwickeln. TBD setzt eine sehr ausgereifte CI/CD-Infrastruktur, konsequente Testautomatisierung und Disziplin im Team voraus. Der DORA-„State of DevOps"-Report zeigt, dass leistungsstarke Teams, die trunk-basiert arbeiten, doppelt so häufig deployen und eine rund dreimal niedrigere Änderungsfehlerrate aufweisen als Teams mit langlebigen Branch-Strategien. Trunk-Based Development wird u. a. bei Google und Facebook eingesetzt und passt besonders gut zu kleinen, synchron arbeitenden Teams mit hoher CI/CD-Reife; für große, verteilte Teams mit klaren Release-Terminen kann Git Flow hingegen leichter verständliche Regeln und Planbarkeit bieten.

**Kurzer Gesamtvergleich**

| Kriterium | Git Flow | GitHub Flow | Trunk-Based Development |
|---|---|---|---|
| Anzahl dauerhafter Branches | 2 (main, develop) | 1 (main) | 1 (Trunk/main) |
| Branch-Lebensdauer | oft lang | kurz | sehr kurz (< 1 Tag) |
| Eignet sich für | geplante Releases, große Teams | kontinuierliche Bereitstellung, kleine Teams | sehr hohe Deploy-Frequenz, hohe CI/CD-Reife |
| Voraussetzung | klare Prozessregeln | Pull-Request-Kultur | starke Testautomatisierung, Feature Flags |

**Quellen:**
- LaunchDarkly – Git Branching Strategies vs. Trunk-Based Development: https://launchdarkly.com/blog/git-branching-strategies-vs-trunk-based-development/
- Inventive HQ – Git Branching Strategies: GitFlow vs GitHub Flow vs Trunk-Based: https://inventivehq.com/blog/git-branching-strategies-guide
- env.dev – Git Branching Strategies: GitFlow, Trunk-Based & GitHub Flow: https://env.dev/guides/git-branching-strategies
- DEV Community – Comparing Git branching strategies: https://dev.to/arbitrarybytes/comparing-git-branching-strategies-dl4
- Assembla – Trunk-Based Development Vs Git Flow: A Comparison: https://get.assembla.com/blog/trunk-based-development-vs-git-flow/
- Karwl's Blog – Git Branching Strategien: https://www.karwlblog.de/git-branching-strategien/

## 5. Wie kann man Commits und Branches mit User Stories verknüpfen?

### Warum diese Verknüpfung sinnvoll ist
Wenn Codeänderungen mit User Stories bzw. Tickets aus dem Aufgabenmanagement (z. B. Jira) verknüpft werden, entsteht eine durchgängige Nachvollziehbarkeit: Man kann jederzeit sehen, welcher Commit oder Branch zu welcher fachlichen Anforderung gehört, wann eine Story tatsächlich umgesetzt und deployt wurde und welche Codeänderungen ein bestimmtes Feature betreffen. Das erleichtert Code-Reviews, Fehlersuche, Aufwandsschätzung für zukünftige Stories und die Erstellung von Release Notes.

### Praktiken und Namenskonventionen

Eine verbreitete Praxis ist, den Branch-Namen mit der Ticket-ID zu versehen, etwa im Format `feature/PROJ-123-kurzbeschreibung` oder `PROJ-123/kurzbeschreibung`. Es haben sich zwei Hauptstile etabliert: durch Bindestrich (`-`) oder durch Schrägstrich (`/`) getrennte Bestandteile. Ergänzend wird empfohlen, jeden Commit ebenfalls mit der Ticket-ID zu präfixen, z. B. `PROJ-123: Login-Funktion implementiert`, da einige Integrationswerkzeuge (etwa GitHub-für-Jira) einen Abgleich zwischen Deployment und Ticket nur zuverlässig herstellen können, wenn die ID auch im Commit selbst und nicht nur im (ggf. gelöschten) Branch-Namen steht.

Für Pull Requests gilt dasselbe Prinzip: Titel und Beschreibung sollten die Ticket-ID referenzieren, häufig ergänzt um einen direkten Link zum Ticket, damit Reviewer den fachlichen Kontext sofort einsehen können.

### Wie Tools die Verbindung zwischen Aufgabenmanagement und Code-Repositories unterstützen

Mehrere Mechanismen automatisieren diese Verknüpfung:

- **Git-Hooks** (z. B. über `husky` mit dem Paket `jira-prepare-commit-msg`) lesen die Ticket-ID automatisch aus dem aktuellen Branch-Namen aus und fügen sie der Commit-Nachricht hinzu, falls der Entwickler sie vergessen hat.
- **Push Rules** in Plattformen wie GitLab können erzwingen, dass jeder Commit eine gültige Ticket-Referenz (z. B. per regulärem Ausdruck `JIRA-\d+`) enthalten muss.

**Quellen:**
- Medium (Suprabha Supi) – Github Branch Naming Convention: https://suprabhasupi.medium.com/github-branch-naming-convention-d517d12cf96
- Medium (Akhil Regonda) – Best Practices for Git Branch Naming Conventions and PR Creation: https://medium.com/@regondaakhil/best-practices-for-git-branch-naming-conventions-and-pr-creation-on-github-14a451d345dc
- GitHub – jira-prepare-commit-msg: https://github.com/webdevnerdstuff/jira-prepare-commit-msg
- Medium (Nick Lee) – Prepending Your Git Commit Messages with User Story IDs: https://medium.com/@nicklee1/prepending-your-git-commit-messages-with-user-story-ids-3bfea00eab5a

## 6. Welche Merge-Strategien gibt es, und wann werden sie verwendet?

### Ansätze zur Integration von Änderungen aus einem Branch in einen anderen

Es gibt in Git im Wesentlichen drei zentrale Strategien, um Änderungen von einem Branch in einen anderen zu übernehmen: **Merge**, **Rebase** und **Squash**.

- **Merge (Merge Commit):** Git erzeugt einen neuen Commit mit zwei Elternknoten, der die vollständige Historie beider Branches inklusive aller Einzel-Commits zusammenführt. Dies ist die Standard-Strategie und für viele die erste, die sie lernen.
- **Rebase:** Die Commits des Feature-Branches werden „zurückgespult" und einzeln, mit neuen Commit-Hashes, auf den aktuellen Stand des Zielbranches „aufgesetzt" (replayed). Es entsteht eine lineare Historie ohne separate Merge-Commits.
- **Squash (Squash-Merge):** Alle Einzel-Commits eines Feature-Branches werden zu einem einzigen neuen Commit zusammengefasst, bevor dieser in den Zielbranch übernommen wird; die granulare Commit-Historie des Feature-Branches geht dabei verloren.

### Einfluss auf Historie und Nachvollziehbarkeit

- Merge bewahrt die vollständige Historie, führt aber zu einer „unordentlicheren" Historie mit vielen Merge-Commits, was die Fehlersuche erschweren kann.
- Rebase erzeugt eine **übersichtliche Historie**, birgt aber ein erhebliches Risiko: Wird ein bereits von anderen genutzter (öffentlicher) Branch rebased, wird dessen Historie umgeschrieben, sodass andere Entwickler, die davon bereits abgezweigt haben, ihre eigene Arbeit nicht mehr konfliktfrei zusammenführen können.
- Squash liefert die **übersichtlichste** Historie im Zielbranch (ein Commit pro Feature), verschleiert dabei aber die einzelnen Entwicklungsschritte; bei sehr aktiven Feature-Branches kann Squash den Hauptbranch „verschmutzen", wenn zu viele kleine, für die Historie irrelevante Zwischen-Commits gebündelt werden.

### Unter welchen Umständen welche Strategie bevorzugt wird

- **Merge** eignet sich, wenn die vollständige Nachvollziehbarkeit der Entwicklung wichtig ist, insbesondere bei öffentlichen/gemeinsam genutzten Branches, bei denen ein Umschreiben der Historie riskant wäre.
- **Rebase** eignet sich, wenn ein einzelner Entwickler seinen (noch nicht geteilten) Feature-Branch auf den neuesten Stand des Hauptbranches bringen möchte, um eine saubere, lineare Historie zu erhalten und Konflikte frühzeitig einzeln aufzulösen. Rebase auf bereits veröffentlichten, von anderen genutzten Branches sollte grundsätzlich vermieden werden.
- **Squash** eignet sich besonders, wenn viele kleine, für sich genommen unwichtige Zwischen-Commits (Lint-Fixes, fehlgeschlagene Testläufe, Formatierungskorrekturen) zu einem sauberen, aussagekräftigen Commit zusammengefasst werden sollen, bevor das Feature in den Hauptbranch übernommen wird.

In der Praxis kombinieren viele Teams die Ansätze: lokal wird per Rebase der eigene Branch aktuell gehalten, die finale Integration in den Hauptbranch erfolgt dann per (Squash-)Merge.

**Quellen:**
- Medium (Davide Rubinetti) – Git rebase vs merge vs squash: https://medium.com/@davide.rubinetti97/git-rebase-vs-merge-vs-squash-different-strategies-explained-7df068f37578
- Medium (Ritu Shikha) – Git Rebase vs Merge vs Squash: https://medium.com/@shikha.ritu17/git-rebase-vs-merge-vs-squash-choosing-the-right-strategy-for-version-control-a9c9bb97040e
- LeanIX Engineering – Mastering Git Merge Strategies: https://engineering.leanix.net/blog/merge-techniques/
- OneUptime – How to Handle Git Rebase vs Merge Strategies: https://oneuptime.com/blog/post/2026-01-24-git-rebase-vs-merge-strategies/view
- Tech Tutorials – Git Rebase vs Merge vs Squash: When to Use Each: https://tutorials.technology/tutorials/git-rebase-vs-merge-vs-squash-2026.html

## 7. Was ist Semantic Versioning, und wie wird es eingesetzt?
### Hilfe bei der Verwaltung von Software-Versionen
Semantic Versioning ist ein Konzept zur Nummerierung von Software-Releases, das ein reales Problem löst: die sogenannte „Dependency Hell". In grossen Projekten mit vielen Abhängigkeiten kann das Aktualisieren von Paketen sonst schnell unübersichtlich werden, weil unklar ist, ob ein Update sicher ist oder bestehenden Code zerstört. SemVer schafft ein einheitliches, vorhersagbares Format, mit dem Versionsnummern selbst schon Auskunft über die Art der enthaltenen Änderungen geben.

### Konventionen bei der Anwendung

SemVer verwendet ein dreiteiliges Format: **MAJOR.MINOR.PATCH** (z. B. `2.4.1`). Die Regeln zur Erhöhung sind klar definiert:

- **MAJOR** wird erhöht, wenn inkompatible (breaking) Änderungen an der öffentlichen API veröffentlicht werden. Beim Erhöhen von MAJOR werden MINOR und PATCH auf 0 zurückgesetzt.
- **MINOR** wird erhöht, wenn neue, abwärtskompatible Funktionalität hinzugefügt wird.
- **PATCH** wird erhöht, wenn ausschliesslich abwärtskompatible Bugfixes vorgenommen werden, die die API nicht verändern.

Zusätzlich erlaubt SemVer optionale Erweiterungen für Pre-Release-Kennzeichnungen (z. B. `2.0.0-alpha.1`) und Build-Metadaten, die bei der Bestimmung der Versionsrangfolge ignoriert werden. Voraussetzung für die Anwendung von SemVer ist, dass ein Projekt eine klar definierte, öffentliche API besitzt, gegen die sich Änderungen einordnen lassen.

### Warum Semantic Versioning für Kompatibilität und Kommunikation wichtig ist

SemVer ist vor allem für das Abhängigkeitsmanagement zentral: Paketmanager wie npm verwenden Bereichsoperatoren (`^`, `~`) auf Basis von SemVer, um automatisch zu entscheiden, welche neuen Versionen einer Abhängigkeit sicher automatisch übernommen werden können (z. B. Patch- und Minor-Updates) und welche nicht (Major-Updates). Damit übernimmt eine Versionsnummer eine kommunikative Funktion: Ein Wechsel von `2.4.1` auf `3.0.0` signalisiert Nutzern sofort, dass etwas potenziell Bestehendes brechen könnte, während `2.4.1` auf `2.4.2` einen risikolosen Drop-in-Fix verspricht. In CI/CD-Kontexten wird die konsequente Nutzung von SemVer zudem oft mit Werkzeugen wie `semantic-release` automatisiert, die anhand von Commit-Nachrichten selbstständig die passende neue Versionsnummer bestimmen.

**Quellen:**
- Semantic Versioning 2.0.0 (offizielle Spezifikation, deutsch): https://semver.org/lang/de
- Semantic Versioning 2.0.0 (GitHub-Repository, englisch): https://github.com/semver/semver/blob/master/semver.md
- Real Python – semantic versioning (SemVer): https://realpython.com/ref/software-engineering-glossary/semantic-versioning/
- ByteByteGo – What do version numbers mean?: https://bytebytego.com/guides/what-do-version-numbers-mean/
- Baeldung on Computer Science – A Guide to Semantic Versioning: https://www.baeldung.com/cs/semantic-versioning
- OpenReplay Blog – Semantic Versioning Explained: https://blog.openreplay.com/semantic-versioning-explained/

## 8. Welchen Unterschied haben Mono- und Multirepo-Ansätze im Kontext von Microservices?
### Unterschiede in der Organisation von Code
Beim Monorepo-Ansatz liegen alle Services bzw. Bibliotheken eines Unternehmens in einem einzigen, gemeinsam genutzten Repository. Es gibt eine einzige Git-Historie und in der Regel eine gemeinsame, vereinheitlichte CI/CD-Pipeline für alle enthaltenen Komponenten.

Beim Multirepo-Ansatz erhält jeder Microservice sein eigenes, unabhängiges Repository. Jedes Repository hat eine eigene Historie, eigene Versionierung und eine eigene, isolierte CI/CD-Pipeline.

### Vor- und Nachteile für Entwicklung und Wartung von Microservices

**Monorepo – Vorteile:**
- Änderungen an mehreren Services können in einem einzigen Pull Request gebündelt werden; Tests für alle betroffenen Services lassen sich zentral ausführen.
- Eine einzige, klar nachvollziehbare Git-Historie erleichtert Changelogs, Dokumentation und das Nachvollziehen, welche Service-Versionen zu einem bestimmten Zeitpunkt gemeinsam produktiv liefen, was Rollbacks deutlich vereinfacht.
- Ein einziger CI/CD-Prozess standardisiert Testen und Deployment über alle Services hinweg.

**Monorepo – Nachteile:**
- Der CI-Server steht unter höherer Last, da alle Änderungen zentral zusammenlaufen.
- Beiträger müssen sich zunächst im großen Repository orientieren und verstehen, wie ihre Änderung sich auf andere Projekte auswirken könnte.

**Multirepo – Vorteile:**
- Klare Ownership: Contributoren gehen direkt zum relevanten Repository, ohne sich durch fremden Code navigieren zu müssen.
- Jeder Service kann unabhängig getaggt und versioniert werden.
- Isolierte CI/CD-Pipelines: Eine Änderung an Service A löst keine Tests für Service B aus, was einzelne Pipelines schneller macht und die Zugriffssteuerung vereinfacht.
- Multirepos sind gut für kontinuierliches, unabhängiges Deployment einzelner Services geeignet.

**Multirepo – Nachteile:**
- Es entsteht kein zentraler Schnappschuss davon, welche Versionen der einzelnen Services zu einem bestimmten Zeitpunkt zusammen in Produktion liefen; die Beziehungen zwischen Services sind nicht in einer einzigen Historie erfasst.
- Dadurch wird die Diagnose von Integrationsproblemen zeitaufwändiger, und im Fehlerfall kann ein Rollback über mehrere Services hinweg erschwert sein.
- Multirepo erfordert mehr Disziplin, um Abhängigkeiten zwischen Services konsistent zu halten – jedes Projekt muss seine Abhängigkeiten aktualisieren, um von Qualitätsverbesserungen oder Sicherheitskorrekturen anderer Services zu profitieren.

### Auswirkungen auf Skalierbarkeit, Zusammenarbeit und Abhängigkeiten zwischen Teams

Grosse Unternehmen wie Google, Airbnb und Uber setzen erfolgreich auf Monorepos für ihre Microservices, weil dies den Umgang mit vielen Services „wie einen Monolithen" vereinfacht, jedoch nur in Kombination mit entsprechend leistungsfähiger CI/CD-Infrastruktur, da alle Änderungen den gleichen zentralen Prozess durchlaufen. Multirepo passt dagegen besser zur organisatorischen Autonomie einzelner Teams, wie sie der Microservices-Gedanke ursprünglich vorsieht: Jedes Team besitzt und verantwortet sein Repository vollständig, muss dafür aber eigenständig Sorge tragen, dass seine Abhängigkeiten zu anderen Services aktuell und kompatibel bleiben. Die Wahl hängt letztlich von Teamgröße, technischer Reife und der gewünschten Kopplung zwischen den Services ab.

**Quellen:**
- Semaphore – Release Management for Microservices: Multirepo vs Monorepo: https://semaphore.io/blog/release-management-microservices
- Kinsta – Monorepo vs. Multi-Repo: Vor- und Nachteile: https://kinsta.com/blog/monorepo-vs-multi-repo/
- Infomaniak News – Multirepo vs Monorepo (Erfahrungsbericht): https://news.infomaniak.com/multirepo-vs-microservices/
- Waldheim.dev – Git-Repository-Struktur: Blaupause für skalierbare Monorepos und Polyrepos: https://www.waldheim.dev/blog/git-repository-struktur-meine-blaupause-f%C3%BCr-skalierbare-monorepos-und-polyrepos/

## 9. Was ist ein Artifact-Repository, und welche Aufgaben erfüllt es?

### Rolle in der Softwareentwicklung und im Bereitstellungsprozess

Ein Artifact-Repository (Artefakt-Repository) ist ein zentraler Speicherort für die **binären Ergebnisse** eines Build-Prozesses – im Gegensatz zu einem Git-Repository, das den Quellcode verwaltet, verwaltet ein Artifact-Repository die daraus erzeugten, kompilierten oder gepackten Artefakte. Es fungiert damit als „Single Source of Truth" für alle Binärdateien, von extern bezogenen Abhängigkeiten bis zu selbst erstellten Build-Ergebnissen, und beschleunigt so den automatisierten Build-, Test- und Deployment-Prozess innerhalb der CI/CD-Pipeline. Typische Vertreter sind Sonatype Nexus Repository, JFrog Artifactory und Apache Archiva.

### Arten von Artefakten, die typischerweise verwaltet werden

Ein Artifact-Repository speichert und versioniert ganz unterschiedliche Artefakttypen, etwa:
- kompilierte Bibliotheken und Pakete (z. B. Java-JARs, npm-Pakete, PyPI-Pakete, NuGet-Pakete),
- Container-Images (z. B. Docker-Images),
- Helm-Charts für Kubernetes-Deployments,
- sowie zugehörige Metadaten wie Build-Informationen, Abhängigkeitsgraphen und Herkunftsnachweise (wer hat wann was veröffentlicht).

Ein typischer Workflow lautet dabei: Code → Build → Test → Artefakt paketieren → Push in das Repository (Nexus/Artifactory) → Deployment in die Zielumgebung.

### Warum ein Artifact-Repository für CI/CD-Pipelines wichtig ist

Ohne ein zentrales Artifact-Repository würden CI/CD-Pipelines mehrfach dieselben Abhängigkeiten aus dem Internet herunterladen, was Builds verlangsamt, sie von der Verfügbarkeit externer Quellen abhängig macht und die Reproduzierbarkeit gefährdet (Builds könnten brechen, wenn eine externe Abhängigkeit plötzlich nicht mehr verfügbar ist). Artifact-Repositories lösen dieses Problem, indem sie:

- als **Cache/Proxy** für externe Abhängigkeiten dienen und diese lokal zwischenspeichern, was Builds beschleunigt,
- ein vollständiges **Audit-Trail** führen: Was wurde wann von wem veröffentlicht, sodass jede Version nachvollziehbar bleibt,
- **Promotion-Workflows** ermöglichen, mit denen Artefakte kontrolliert von Entwicklungs- über Staging- bis zur Produktionsumgebung weitergereicht werden,
- **Sicherheits- und Compliance-Prüfungen** integrieren (z. B. JFrog Xray oder Sonatype Repository Firewall), um bekannte Schwachstellen in Abhängigkeiten frühzeitig zu erkennen,
- über **Retention-Policies** veraltete Artefakte automatisch bereinigen, um Speicherplatz und Übersichtlichkeit zu bewahren.

Damit sind Artifact-Repositories nicht nur reine Ablagen, sondern ein zentraler Bestandteil einer sicheren, effizienten und nachvollziehbaren Software-Lieferkette und ein wesentlicher Baustein moderner CI/CD-Prozesse.

**Quellen:**
- Sonatype – Sonatype Nexus Repository: https://www.sonatype.com/products/sonatype-nexus-repository
- DevOps Training Institute – What Is the Purpose of Artifactory or Nexus?: https://www.devopstraininginstitute.com/blog/what-is-the-purpose-of-artifactory-or-nexus-in-devops-artifact-management
- GeeksforGeeks – Nexus Use Cases For Artifact Repository Management: https://www.geeksforgeeks.org/devops/nexus-use-cases-for-artifact-repository-management/
- Networkers Home – Artifact Management: Nexus, JFrog & Container Registries: https://www.networkershome.com/fundamentals/devops/artifact-management-nexus-jfrog-registries/
- DEV Community (Matt Frank) – Artifact Management: Docker Registry, Nexus, and Artifactory: https://dev.to/matt_frank_usa/artifact-management-docker-registry-nexus-and-artifactory-h1n

---

## Transfer zu P3 / P3b (eigene CI-Umsetzung)

Für die spätere Umsetzung im eigenen Projekt (P3, P3b) lassen sich aus den obigen Theorieaussagen konkrete Handlungsempfehlungen ableiten:

- **CI/Continuous Testing:** Von Beginn an ein zentrales Repository (z. B. GitHub/GitLab) mit angebundenem CI-Server (z. B. GitHub Actions/GitLab CI) einrichten und automatisierte Tests (mindestens Unit-Tests) so früh wie möglich schreiben, statt Tests erst am Ende nachzuziehen.
- **Branching-Strategie:** Für ein kleines Projektteam bietet sich eine schlanke Strategie wie **GitHub Flow** oder ein einfaches **Trunk-Based**-Vorgehen mit kurzlebigen Feature-Branches an, da Git Flow für kleine Teams mit häufigen Releases meist unnötig komplex ist.
- **Verknüpfung mit User Stories:** Branches und Commits konsequent mit der jeweiligen Story-/Ticket-ID benennen (z. B. `feature/P3-12-login`), um Nachvollziehbarkeit zwischen Aufgabenmanagement und Code sicherzustellen.
- **Merge-Strategie:** Für die Zusammenführung von Feature-Branches in den Hauptbranch bietet sich Squash-Merge an, um eine übersichtliche Historie im Hauptbranch zu erhalten, während lokal mit Rebase gearbeitet werden kann.
- **Versionierung:** Releases konsequent nach Semantic Versioning kennzeichnen (`MAJOR.MINOR.PATCH`), sobald eine erste stabile Version (1.0.0) vorliegt.
- **Repository-Struktur/Artefakt-Repository:** Bei einem einzelnen, überschaubaren P3-Projekt reicht in der Regel ein einfaches Repository (Monorepo-Charakter); bei P3b mit mehreren unabhängigen Diensten sollte frühzeitig überlegt werden, ob ein Multirepo-Ansatz mit einem gemeinsamen Artifact-Repository (z. B. zur Verwaltung von Docker-Images) sinnvoll ist.

---

## Hinweis zur KI-Nutzung

Dieses Dokument wurde unter Einsatz von Claude erstellt. Die Inhalte wurden auf Basis mehrerer, oben zitierter Web-Quellen recherchiert, zusammengeführt und in eigenen Worten formuliert.
