# Arbeitsbericht Lukas

#### Grobe Auflistung der Tätigkeiten
- Layout-Gestaltung von
    1. Eventliste/Positionsliste 
    2. Event/Positions-Erstellung
    3. Event/Positions-Info
    4. Profil-Ansicht
    5. Einladungs-Link
    6. Bilanz
    7. Nutzerliste
- Layout/ Zustandsänderungen für Button- clicks
- Anpassung der Views auf diverse Zustände in der App
- UI-Bugfixes
- optische Gestaltung und Verschönerung
- teilweise Abfragen zur Einschränkung des User-Input (z.B. EventEndDate !< EventStartDate)
- teilweise statistische Werte berechnen (z.B. Anteil eines User an den Gesamtkosten)


## Arbeitsaufwand


Mein Gesamtaufwand im Projekt sieht wie folgt aus:

| Tätigkeit                  | Aufwand   |
|----------------------------|:---------:|
| Layoutgestaltung           | ~58h      |
| Projektabsprachen          | ~36h      |
| Button-Events              | ~22h      |
| View-Anpassungen           | ~15h      |
| optische Verschönerung     | ~10h      |
| Bugfixes                   | ~8h       |
| Sonstiges                  | ~7h       |
| Doku/Präsentation          | ~5h       |
| **Summe**                  | **~161h** |
| geschätzter Aufwand        | **135<sup>\*</sup>**|


<sup>\*</sup> Der geschätze Gesamtaufwand basiert auf folgender Annahme: 9CP = 270h. Das Projekt macht 50% des Moduls aus. 50% von 270h = 135h.

_____


##	Layoutgestaltung

**Gesamtaufwand**: ~58h
- Erstellung eines Fragments, das sowohl Event- als auch Positionslisten grafisch darstellen kann
- Layout zur Erstellung von Events/Positionen einschließlich Abfragen im Input, sodass nur auf das model 'Event' oder 'Position' zugeschnittener Input in die Datenbank gelangt
- Dialog zur Anzeige von Event/Positions-Infos
- Dialog zur Anzeige eines Nutzerprofils
- Dialog 'Invite', welcher drei Möglichkeiten zum Teilen eines Invite-Links bietet
- Fragment 'Cash', das eine globale oder eventbezogene Bilanz des Nutzers gegenüber anderen Nutzern aufstellt (Schulden oder Forderungen?)
- Fragment 'UserList', welches die Auswahl eines oder mehrere Nutzer ermöglicht oder eine Nutzerliste anzeigt 


##	Projektabsprachen

**Gesamtaufwand**: ~36h
- Regelmäßige wöchentliche Meetings (Discord)
- oftmals Besprechung unter vier Augen bzgl. optischer Wünsche oder aufgetretener Bugs im UI
- oftmals Beantwortung von Nachfragen nach Zeilen/ Buttons für Auslöser diverser Events


## Button-Events

**Gesamtaufwand**: ~22h
- Aktualisierung/ Löschung von Einträgen in der Datenbank bei Button-Events
- Anpassung der Darstellung bei bestimmten Button-Events (z.B Ausblenden anderer Views)
- Aufruf neuer Activities/ Dialogs bei Button-Click


## View-Anpassungen

**Gesamtaufwand**: ~15h
- (Live-)Aktualisierung von Listen/ Werten nach Änderung in der Datenbank
- Einblenden/ Ausblenden von Hilfetexten/ Buttons/ Images je nach Zustand 


## Optische Verschönerung

**Gesamtaufwand**: ~10h
- Festlegung auf ein bestimmtes Design sowie Anpassung dessen auf das optische Gesamtbild der App
- Schriftgrößen/Farben passend abstimmen
- Bilder für besseres Verständnis
- interaktive, benutzerfreundliche Dialoge


## Bugfixes

**Gesamtaufwand**: ~8h
- Listen aktualisieren sich nicht
- Auftrtitt ungewollter Zustände in der GUI
- viele, viele NullPointer :0


## Sonstiges

**Gesamtaufwand**: ~7h
- Strings zur Sprache erstellen/ ändern
- Benutzerfreundlichkeit/ Interaktion verbessern
- aktives Testing


## Projektdokumentation & Präsentationsvorbereitung

**Gesamtaufwand**: ~5h

- Technische Doku
    - Layout
    - Buttons
    - JavaDocs   
- Präsentation

