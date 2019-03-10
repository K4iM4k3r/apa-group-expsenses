
## Einleitung

Meine Aufgaben innerhalb des Projekts lassen sich grob als Koordination, Modellierung, Logik und Testing zusammenfassen. Details hierzu sind dem nächsten Abschnitt zu entnehmen.

Mein Gesamtaufwand im Projekt sieht wie folgt aus:

| Tätigkeit                 | Aufwand (real)  | Aufwand (geschätzt) |
|---------------------------|:---------------:|:-------------------:|
|Projektkoordination        | ~50h            | 30h                 |
|Modellierung d. Projekts   | ~15h            | 15h                 |
|Lifecycle                  | ~20h            | --                  |
|Invite-Links               | ~5h             | 15h                 |
|Sonstiges                  | ~20h            | --                  |
|Testing                    | ~15h            | --                  |
|Doku/Präsentation          | ~20h            | --                  |
|**Summe**                  | **~145h**       | **135<sup>\*</sup>**|


<sup>\*</sup> Der geschätze Gesamtaufwand basiert auf folgender Annahme: 9CP = 270h. Das Projekt macht 50% des Moduls aus. 50% von 270h = 135h.

_____

##	Projektkoordination

**Gesamtaufwand**: ~50h (30h)  
**Anmerkungen**: Teilweise sehr ausführliche Meetings + Diskussionen.

- Discord Server für Teammeetings / Research
- Issues für Github
- später: Projektleiter Tätigkeiten
 - Leitung von Teammeetings (wöchentlich)
 - Vorantreiben des Projektstandes
 - Regelmäßige Absprachen bezüglich projektspezifischer Fragen (zb Design, Features)

##	Modellierung des Projekts

**Gesamtaufwand**: ~15h (15h)  

- Mockup erstellen
- Umsetzung Mockup in Grundstruktur der App
 - Struktur schaffen (durch packages)
 - Activites grob anlegen
- Modellklassen generieren
 - Event
 - User
 - Position (Expenses)

## Integration Ausgabenverwaltung (aka Kassensturz)

**Gesamtaufwand**: ~20h (15h)  
**Anmerkungen**: Die durch die Datenbank vorgegebene Struktur hat zu einer komplexeren Lösung geführt als ursprünglich angenommen.

- Evaluierung der Möglichkeiten (iA KS)
 - Firebase Datensystem möglichst effektiv nutzen
 - saubere Datenstruktur für Datenbank schaffen
 - möglichst wenig Informationen in Datenbank speichern um Datenverkehr gering zu halten
- Integration von Methoden zur Verwaltung von Ausgaben
 - Erstellung eines "Balance Tables" für einen Nutzer -> zeigt alle Schuldnerverhältnisse des gegebenen Nutzers

## Event Lifecycle Implementierung

**Gesamtaufwand**: ~20h (--)  
**Anmerkungen**: Der Aufwand wurde im Voraus nicht geschätzt. Der großteil der Arbeitszeit fließ in die GUI Integration.

- Bestimmung des derzeitgen Eventstand anhand der Variablen ```date_begin```, ```date_end``` und ```date_deadlineDay```
- Kategorisierung in ```UPCOMING```, ```LIVE```, ```PAYTIME```, ```CLOSED```und ```ERROR```
- Integration in GUI
 - Fabs in PositionAcitvity (iA KS)
 - Statusicon in Eventliste (später überarbeitet durch LH)
 - addMember Verhalten & Icon angepasst an Lifecycle

## Invite-Links

**Gesamtaufwand**: ~5h (15h)  
**Anmerkungen**: Der Umfang dieser Aufgabe war deutlich geringer als erwartet.

- Erstellung von Einladungslinks zu Events
- App Reaktion auf Links
- Teilen per Mail, WhatsApp, Clipboard, Twitter<sup>\*</sup>, Facebook<sup>\*</sup>, Google+<sup>\*</sup>
- GUI-Umsetzung durch LH
- HTML Seite falls App nicht installiert durch KS


<sup>\*</sup> nicht über GUI erreichbar, nur technisch integriert

## Sonstiges

**Gesamtaufwand**: ~20h (--)  
**Anmerkungen**: Hier sind einige kleinere Tätigkeiten vermerkt. GUI-Optimierungen beschreibt lediglich Optimierungen - der Großteil der jeweiligen UI war schon existent. Da es sich hier um die Summe vieler kleiner Aufgaben handelt, gibt es keine Aufwandsschätzung.

- GUI-Optimierungen:
 - ProfileView edit/save Button in ActionBar (statt Button)
 - EventForm Anpassung
- Fremdcode Refactoring
 - PositionAcitvity
 - ProfileActivity
 - EventFormActivtiy
- PayPal Bezahlsystem über PayPal.me<sup>\*</sup>

<sup>\*</sup> nicht ins Projekt übernommen. Umfangreichere Ansatz von David Omran implementiert.


## Testing

**Gesamtaufwand**: ~15h

- regelmäßiges durchführen von Funktionstests
- Optimierungsmöglichkeiten suchen
- selbsständige Fehlerbehebung oder Generierung von Issues


## Projektdokumentation & Präsentationsvorbereitung

**Gesamtaufwand**: ~20h

_____
Abkürzungen:
- in Absprache: iA
- zum Beispiel: zB
- Kai Schäfer: KS
- Lukas Hilfrich: LH

Anmerkungen
- Aufwand: real (estimated)
- Der Gesamtaufwand wurde nicht real zeitlich gemessen, sondern nach Abschluss der Tätigkeit grob geschätzt.
