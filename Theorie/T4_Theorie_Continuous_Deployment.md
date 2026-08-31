## Continuous Deployment, Umsetzung
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



https://www.atlassian.com/continuous-delivery/principles/continuous-integration-vs-delivery-vs-deployment
https://en.wikipedia.org/wiki/Continuous_deployment
https://docs.aws.amazon.com/whitepapers/latest/overview-deployment-options/canary-deployments.html


