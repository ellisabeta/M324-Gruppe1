# T2 DevOps_Prozesse

## SDLC Software Development Life Cycle
Der Softwareentwicklungszyklus ist ein Prozess der von Entwicklungsteams verwendet wird um Zeit und Kosten zu sparen. Dabei definiert man verschiedene Ziele und Aufgaben für jede Phase, während jeder Phase können Entwickler neue Funktionen hinzufügen und Fehler beheben. 

Insgesamt durchläuft dieser Prozess 7 Phasen, diese sind nicht fix definiert und variieren je nach Quelle.

**Planen**
- Kosten-Nutzen Analyse
- Zeitplanung
- Kosteneinschätzung
- Ressourcenschätzung und -zuweisung
- Anforderungen kennen von Stakeholdern
- Detaillierter Plan für Zielerreichung
- (Teststrategie)


**Analyse**
- Anforderungen sammeln
- Funktionale und Nicht funktionale Anforderungen analysieren
- Use Case Szenarien
- User Stories
- Personas
- Dokumentation
- Feedback Loop


**Design/Entwerfen**
- UX/UI Design
- Prototyp und Mockups
- Design und Komponenten festlegen


**Implementieren**
- Schreiben des Codes
- Unit tests


**Testen**
- Test Planung und Strategie umsetzen
- Unit tests
- Integration Tests
- System Tests
- User Acceptance Tests

  
**Bereitstellen**
- Code in Produktionsumgebung bereitstellen
- Zugriff für Benutzer ermöglichen
- Software in Phasen bereitstellen (z.B Beta Version)


**Wartung**
- Updates und Optimierungen
- Wartung und Unterstützung
- Fehler beheben
- ev. kontinuierliche Integration und kontinuierliche Bereitstellung (CI/CD)
- 

<img width="925" height="500" alt="image" src="https://github.com/user-attachments/assets/7fc7ef84-81fb-4bf8-bd28-27244defa37f" />

<img width="676" height="362" alt="image" src="https://github.com/user-attachments/assets/16b029a5-ce3a-4012-b8e1-bf2188830259" />

## Unterschiede SDLC und DevOps Lifecycle

- SDLC - Software Development Life Cycle beschreibt die Phasen der Softwareentwicklung und fokussiert sich auf systematische Softwareentwicklung, wodurch Fehler reduziert und die **Effizienz** gesteigert werden.
- DevOps Life Cycle optimiert die Zusammenarbeit zwischen development team und operations team,  **Automatisierung** von Prozessen und der Gewährleistung reibungsloser Bereitstellungen.

| SDLC Phasen    | DevOps Lifecycle |
| -------- | ------- |
| Plan     | Plan    |
| Define   | Code    |
| Design   | Build   |
| Development | Test |
| Testing  | Release |
| Deploy   | Deploy  |
| Maintain  | Operate |
| -        | Monitor |

<img src="https://miro.medium.com/v2/resize:fit:828/format:webp/1*d2MmOnSxHxbcyaLOCuO_Aw.jpeg">

### DevOps Prinzipien: 
- Zusammenarbeit
- Automatisierung
- Kontinuierliche Integration (CI)
- Continuous Delivery (CD)
- Überwachung und Feedback

DevOps ist ein kontinuierlicher Loop während SDLC ist ein Phasen-prozess.  
Die Zeile von DevOps und SDLC unterscheiden sich indem, dass DevOps sich auf Geschwindigkeit, Agilität und Kontinuierlichkeit fokussiert und SDLC fokussiert sich auf Qualität, Systematik und Effizienz im Entwicklungsprozess.

## Definition MVP und Bedeutung im DevOps Lifecycle
Ein Minimum Viable Product ist die einfachste Version eines Produktes, welches die grundlegende Features beinhaltet. Das Ziel einer MVP ist so schnell wie möglich das Produkt veröffentlichen und echtes User Feedback sammeln, wodurch das Produkt verbessert werden kann.

Kernfunktionen:
- Time-to-Market verkürzen: schneller live gehen und Wettbewerbsvorteil sichern
- Risiken minimieren: nur Kernfunktionen entwickeln
- Direktes Feedback: echtes Nutzerverhalten statt Annahmen
- Investitionen fokussieren: kein Over-Engineering, sondern messbare Learnings

### Rolle von MVP im DevOps Lifecycle
| DevOps Lifecycle | MVP |
| -------- | ------- |
| Plan     | Es werden nur Features zugelassen, die direkt bei der Kernidee beitragen.    |
| Code     | Entwickler schreiben minimalen, aber funktionsfähigen Code, da das Feature möglicherweise anhand von Nutzerfeedback verworfen oder angepasst wird.    |
| Build    | Kleine Code-Änderungen lassen sich schnell bauen und automatisiert testen.   |
| Test     | Der Fokus liegt auf Kern E2E Tests und Sicherheitsprüfungen, anstatt jede denkbare Edge Case Funktion abzudecken.    |
| Release  | Über kleinere Testgruppe kann ein Release schneller gemacht werden. |
| Deploy   | Dank CI/CD installiert der Code die neueste Version automatisiert und risikofrei auf dem Server.  |
| Operate  | Der Fokus im Betrieb liegtx nicht nur auf Systemstabilität, sondern primär auf dem Sammeln von Nutzer- und Nutzungsdaten. |
| Monitor  | Das reale Verhalten der Nutzer zeigt, ob das MVP die Erwartungen erfüllt. |

## Quellen
- https://aws.amazon.com/de/what-is/sdlc/
- https://medium.productcoalition.com/a-comprehensive-guide-to-the-software-development-life-cycle-sdlc-15b7892e1d44
- https://www.ibm.com/de-de/think/topics/sdlc
- https://medium.com/@nalawade1000work/sdlc-vs-devops-25ff2dd0accf
- https://shwetha.hashnode.dev/sdlc-and-devops-guide-to-building-great-software
- https://www.atlassian.com/agile/product-management/minimum-viable-product
