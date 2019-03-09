Abkürzungen:
- in Absprache: iA
- zum Beispiel: zB
- Kai Schäfer: KS
- Lukas Hilfrich: LH


##	Projektkoordination

Gesamtaufwand: ~50h (exklusive Testing)

- Discord Server für Teammeetings / Research
- Issues für Github
- später: Projektleiter Tätigkeiten
 - Leitung von Teammeetings (wöchentlich)
 - Vorantreiben des Projektstandes
 - Regelmäßige Absprachen bezüglich projektspezifischer Fragen (zb Design, Features)

##	Modellierung des Projekts

Gesamtaufwand: ~20h

- Mockup erstellen
- Umsetzung Mockup in Grundstruktur der App
 - Struktur schaffen (durch packages)
 - Activites grob anlegen
- Modellklassen generieren
 - Event
 - User
 - Position (Expenses)

## Integration Ausgabenverwaltung (aka Kassensturz)

Gesamtaufwand: ~25h

- Evaluierung der Möglichkeiten (iA KS)
 - Firebase Datensystem möglichst effektiv nutzen
 - saubere Datenstruktur für Datenbank schaffen
 - möglichst wenig Informationen in Datenbank speichern um Datenverkehr gering zu halten
- Integration von Methoden zur Verwaltung von Ausgaben
 - Erstellung eines "Balance Tables" für einen Nutzer -> zeigt alle Schuldnerverhältnisse des gegebenen Nutzers

## Event Lifecycle Implementierung

Gesamtaufwand: ~20h

- Bestimmung des derzeitgen Eventstand anhand der Variablen ```date_begin```, ```date_end``` und ```date_deadlineDay```
- Kategorisierung in ```UPCOMING```, ```LIVE```, ```PAYTIME```, ```CLOSED```und ```ERROR```
- Integration in GUI
 - Fabs in PositionAcitvity (iA KS)
 - Statusicon in Eventliste (später überarbeitet durch LH)
 - addMember Verhalten & Icon angepasst an Lifecycle

## Invite-Links

Gesamtaufwand: ~5h

- Erstellung von Einladungslinks zu Events
- App Reaktion auf Links
- Teilen per Mail, WhatsApp, Clipboard, Twitter<sup>\*</sup>, Facebook<sup>\*</sup>, Google+<sup>\*</sup>
- GUI-Umsetzung durch LH
- HTML Seite falls App nicht installiert durch KS


<sup>\*</sup> nicht über GUI erreichbar, nur technisch integriert

## Sonstiges

Gesamtaufwand: ~15h

- GUI-Optimierungen:
 - ProfileView edit/save Button in ActionBar (statt Button)
- Fremdcode Refactoring
 - PositionAcitvity
 - ProfileActivity
 - EventFormActivtiy


## Testing

Gesamtaufwand: ~20h

- regelmäßiges durchführen von Funktionstests
- Optimierungsmöglichkeiten suchen
- selbsständige Fehlerbehebung oder Generierung von Issues


## Projektdokumentation & Präsentationsvorbereitung

Gesamtaufwand: ~20h
