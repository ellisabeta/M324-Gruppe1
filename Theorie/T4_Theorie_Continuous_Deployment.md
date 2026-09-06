# T4 Continuous Deployment
## Definition und Umsetzung
Continuous Deployment ist ein Angehensweise beim Software engineering bei dem Funktionalitäten regelmässig mittels automatisierten deployments gepushed werden.

Damit das funktioniert braucht es eine gute test coverage, real time Überwachung und starke kontinuierliche integrations pipelines um zu verhindern das bugs in Prod kommen.
Um diesen Prozess zu übernehmen braucht es auch gute Zusammenarbeit unter stakeholders, dev teams, leadership, operations und Qualitätskontrolle.

Beispiel für Continuous Deployment:
In einer Umgebung, in der datenzentrierte Microservices die Funktionalität bereitstellen und von einem Microservice mehrere Instanzen existieren können, besteht Continuous Deployment darin, die neue Version eines Microservices zu instanziieren und die alte Version außer Betrieb zu nehmen, sobald diese alle noch laufenden Anfragen abgearbeitet hat.
### Vorteile:
- release speed
- Fehler schneller finden
- Gute Kundenerfahrung und Einbindung

### What you need
- Starke Testing Kultur, gute Test Qualität
- guter Dokumentationsprozess
- Feature Flags, hilfreich um changes mit anderen Abteilungen zu koordinieren.

## Continuous Deployment vs Continuous Delivery
Continuous Delivery ist eine Extension von Continuous integration da es automatisch die code changed deployed in eine testing oder prod Umgebung nach der build Phase.

Continuous Deployment ist also ähnlich wie Continuous Delivery nur dass es die Schritte mehr automatisiert.
Automatisierung braucht automatisierte und robuste tests für jedes neue Feature, Improvement oder bug fix.
Man braucht einen continuous integration server der den main repository überwacht und die tests automatisch ausführt bei jedem neuen commit.

Bei continuous delivery ist der trigger manuell aber sobald das deployment gestartet ist braucht es keine manuellen Aktionen mehr. Dafür braucht man wie auch bei Continuous Deployment eine sehr starke Testing Kultur

<img width="960" height="494" alt="image" src="https://github.com/user-attachments/assets/2b1bf54e-eb24-4d96-acb8-638366427fe9" />
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/f504b4fc-d6d4-4988-aa63-da991d449626" />

## Deployment Strategien

### Blue/Green Deployment
Methode um Änderungen zu installieren für Web App oder Datenbank. Dabei tauscht man abwechselnd Prod und Staging Server.

Bei Blue Green Deployments werden zwei Server benötigt, einen grünen und einen blauen. Nur ein Server empfängt die Requests, auf den anderen kann nur über ein privates Netzwerk zugegriffen werden. Die Veränderungen werden dann auf dem Server gemacht der nicht live ist und können auch direkt auf dem privaten Netzwerk getestet werden. Hat das geklappt werden die Server getauscht und die Veränderungen sind live.

Vorteile:
- Risiko und downtime minimieren
- Updates pushen ohne Nachteile für den Benutzer
- Schnelles rollback bei fehlschlagendem deployment

Nachteile:
- Braucht zusätzliche Ressourcen -> teurer
- Doppelte Infrastruktur
- Komplexe Datenbank Migrationen können  schwer sein
- System muss gewährleisten dass beide Umgebungen konsistente Daten haben

Die Blue-Green-Deployment-Technik wird häufig der Canary-Release-Deployment-Technik gegenübergestellt und weist Ähnlichkeiten mit dem A/B-Testing auf.

### Canary Deployment/Release
Features inkrementell testen von einem kleinen Anteil an Benutzer.

Canary Deployment ist eine Art von Blue Green deployment bei dem nur ein Teil der Applikation an die Benutzer delivered wird. Der erste Inkrement ist ein kleiner Anteil des Traffic, diese Gruppe wird dann benutzt um die neue Version zu testen, wenn es erfolgreich ist wird beim zweiten Inkrement den Traffic auf die neue Version geleitet.

Canary Deployments können in zwei Schritten oder linear 

## A/B Testing
AB Testing wird auch Split-Testing und Bucket-Testing gennant, ist eine Methode, um zwei Versionen einer App oder Website zu vergleichen und anhand statistischer Analysen wird entschieden welche Version besser ist.

<img src="https://images.ctfassets.net/dkgr2j75jrom/5Y2u7cESPGJG0gl5Ia3cIv/e77a99b0e58e56e096e14123861f8b0e/a-b-testing-inpagecontent-a-b-testing-process.png?w=1920&h=1177&q=50&fm=webp&bg=transparent">

## Feature Toggles
Ein Feature Toggle ist ein Mechanismus, der es Entwicklern ermöglicht, **bestimmte Funktionen ein- oder auszuschalten, ohne den Quellcode zu verändern**. Zum Beispiel um neue Funktionen zunächst für eine Teilmenge der Nutzer bereitstellen, bevor sie vollständig eingeführt werden.

Bei A/B Testing kann man mit den Feature Toggles verschiedene Implementierungen von Funktionen vergleichen und deren Auswirkungen messen.

Beispiel von einem Feature Toggle: Eine Social-Media-App, die „Stories“ nur für 10 % der Nutzer freischaltet.

## Rollback Strategien
Ein Rollback ist der Prozess, ein System oder eine Anwendung nach einem fehlgeschlagenen Deployment oder einer Änderung in einen vorherigen stabilen Zustand zurückzusetzen.

1. **Manuelles Zurücksetzen**  
Die einfachste Form des Rollbacks – das manuelle Zurücksetzen von Code oder Infrastruktur.

- Vorteile : Unkompliziert für kleine Systeme.
- Nachteile : Zeitaufwändig, fehleranfällig, nicht ideal für große oder hochverfügbare Systeme.

2. **Automatisiertes Rollback über CI/CD-Pipelines**  
CI/CD-Tools wie Jenkins, GitHub Actions oder GitLab können so konfiguriert werden, dass sie Deployments automatisch rückgängig machen, wenn Fehler erkannt werden.

- Vorteile : Schnell und zuverlässig. Reduziert menschliche Fehler.
- Nachteile : Erfordert sorgfältige Einrichtung und Überwachungslogik.

3. **Blau-Grün-Einsatz**  
Die neue Version wird in einer parallelen Umgebung (grün) bereitgestellt, während die alte Version (blau) weiterhin aktiv bleibt. Nach der Validierung wird der Datenverkehr umgeschaltet. Der Rollback leitet den Datenverkehr einfach wieder in die blaue Umgebung um.

- Vorteile : Rollback ohne Ausfallzeiten.
- Nachteile : Verdoppelt vorübergehend die Infrastrukturkosten.

4. **Kanarienvogel-Releases**  
Veröffentlichen Sie die neue Version zunächst für einen kleinen Prozentsatz der Nutzer. Für einen Rollback muss man die Einführung stoppen und für alle Benutzer zur alten Version zurückkehren.

- Vorteile : Minimiertes Risiko, bessere Beobachtbarkeit.
- Nachteile : Erfordert eine robuste Überwachungs- und Datenverkehrsaufteilungslogik.

## Continuous Monitoring und Umsetzung
Die kontinuierliche Überwachung ist die Grundlage dafür, im Geschäftsleben die Aktivitäten des Unternehmens stets im Blick zu behalten.
Kontinuierliches Monitoring ist die **Echtzeitbeobachtung und -analyse** verschiedener Elemente, darunter Cybersicherheit, Netzwerksicherheit, Systemausfallzeiten, Compliance und betriebliche Effizienz.

- Die kontinuierliche Überwachung wird so zu einem proaktiven Schutzschild gegen potenzielle Bedrohungen und Sicherheitslücken.

Best practice für die Umsetzung von Continuous Monitoring:
<img src="https://www.motadata.com/storage/blog/blog-content-images/what-are-the-best-practices-for-continuous-monitoring.png">

Für eine kontinuierliche Überwachung muss man:  
1. **Kennzahlen definieren**
2. **Schwellenstelle festlegen** (Grenzwerte vordefinieren, die Warnmeldungen auslösen)
3. **Benachrichtigungen konfigurieren**
4. **Dashboards optimieren**
5. **Lärm minimieren** (unnötige Warnmeldungen, Datenrauschen)
6. **Redundanz reduzieren** (doppelte Messungen von Kennzahlen vermeiden)
7. **Regelmässige Überprüfung und Anpassung**

## Passwörter sicher speichern
Passwörter werden gehashed damit sie nicht im Klartext sichtbar für alle sind. Es hat das **SHA256-hash** Algorithmus der Verschlüsselung, nicht rückgängig gemacht werden kann. Es gibt auch Salt hashing, der eine zufällige generierte Zeichenkette - ein Salt erstellt. Oft wird **Bcrypt** verwendet um den Passwort zu Salten und dann noch hashen.

Bei CI/CD dürfen Secrets und Passwörter **nicht** im Klartext abgespeichert werden, zum Beispiel: Pipeline configuration files, version control systems oder environment variables. Mann soll keine Passwörter hardcoden. Deswegen verwendet man Geheimnismanagement (secrets management). 

Vorteile:
- Verringerte Angriffsfläche
- Verbesserte Compliance
- Verbesserte Prüfbarkeit
- Zentrale Kontrolle
- Erhöhte Effizienz


## Quellen:
- https://www.atlassian.com/continuous-delivery/principles/continuous-integration-vs-delivery-vs-deployment
- https://en.wikipedia.org/wiki/Continuous_deployment
- https://docs.aws.amazon.com/whitepapers/latest/overview-deployment-options/canary-deployments.html
- https://www.fullstory.com/blog/ab-testing/
- https://dev.to/devcorner/feature-toggle-a-comprehensive-guide-1din
- https://medium.com/@surajpatil141998/rollback-strategies-in-devops-ensuring-safer-deployments-a469243288ac
- https://www.motadata.com/blog/continuous-monitoring
- https://markilott.medium.com/password-storage-basics-2aa9e1586f98
- https://entro.security/glossary/secrets-management-for-ci-cd-pipelines/
