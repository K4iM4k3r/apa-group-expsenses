
# Arbeitsbericht Kai

#### Grobe Auflistung der Tätigkeiten
- FirebaseAccount erstellt und eingerichtet
- Login, Account erstellung, Passwortvergessen
- ProfileActivity, Profileeditierung: Hinzufügen eines Profilbildes und auf dem Storage von Firebase hinterlegen, Editieren der Account Eigenschaften
- Navigation Drawer
- Tabswitcher von Eventliste und Globalen Ausgaben
- Tabswitcher von Event und lokalen Ausgaben
- DatenbankHandler, stetige Anpassungen (LiveData, neue Funktionen, Funktionen überarbeiten)
- kleinere UI Überarbeitungen/Anpassungen, BugFixe
- Bezahlsystem, Server eingerichtet und lauffähig gemacht


## Arbeitsaufwand


Mein Gesamtaufwand im Projekt sieht wie folgt aus:

| Tätigkeit                 | Aufwand   |
|---------------------------|:---------:|
| Projektabsprachen          | ~30h      |
| Accountverwaltung          | ~35h      |
| NavigationDrawer           | ~8h      |
| Datenbank                  | ~35h      |
| Bezahlsystem               | ~4h       |
| Tabswitcher                | ~8h       |
| Sonstiges                  | ~10h       |
| Doku/Präsentation          | ~20h      |
| **Summe**                  | **~150h** |
| geschätzter Aufwand        | **135<sup>\*</sup>**|


<sup>\*</sup> Der geschätze Gesamtaufwand basiert auf folgender Annahme: 9CP = 270h. Das Projekt macht 50% des Moduls aus. 50% von 270h = 135h.

_____

##	Projektabsprachen

**Gesamtaufwand**: ~30h
**Anmerkungen**: Teilweise sehr ausführliche Meetings + Diskussionen.

- Regelmäßige wöchentliche Meetings (Discord)
- Zusätzliche Meetings bei weiteren Fragen


##	Accountverwaltung

**Gesamtaufwand**: ~35h

- FirebaseAccount erstellt und eingerichtet (Appseitig)
- Einarbeitung Auth und Storage von Firebase
- Nutzerkonten
  - Login
  - Accounterstellung(Sign In)
  - Passwort vergessen Möglichkeit
  - Confirmprozess
    - Benachrichtigung das Account noch bestätigt werden muss
    - Screen geht automatisch zu und geht in die Hauptansicht
  - Spätere Überarbeitung des LogIn- und SignInprozesses
- ProfileActivity
  - Hinzufügen eines Profilbildes
    - hinterlegen auf dem FirebaseStorage
    - Anzeige und Editieren der Accounteigenschaften


## NavigationDrawer

**Gesamtaufwand**: ~8h
- Research Implementierung
- Umsetzung
  - Alle Activies (bis auf Login) implementieren eine BaseActivity
    - diese implementiert den NavigationDrawer
    - Vorteil, dass zentrale Funktionen (Progressbar, Auth-Kontrolle)
 - Anzeige von Profilinfos inkl. Profilbild
 - Verknüpfungen zu
  - Profil
  - Main
  - Friends/Kontaktliste
  - Logout


## Datenbank

**Gesamtaufwand**: ~35h

- Einarbeitung in Firestore
- Keine SQLDatenbank
  - Or Queries sind schwer umsetzbar und negierte Queries ebenfalls
  - Collection werden genutzt
  - Vorteil mittels ModelKlasse können diese leicht verwaltet werden
- DatenbankHandler der den Zugriff auf den Firestore ermöglicht
  - erste Entwicklung der Abfragen mittels async Callbacks
  - später dann mit LiveData auf stetige Änderungen zu reagieren
- Erstellen von Datenbankabfragen die von den Teammitgliedern angefragt waren bzw. die erforderlich waren
- Stetige BugFixe

## Bezahlsystem

**Gesamtaufwand**: ~4h

- ServerImplemetierung lauffähig machen (BugFixe)
- externen Server aufsetzten und Code einspielen
- Appseitig handling auf response überarbeiten

## Tabswitcher

**Gesamtaufwand**: ~8h

- Research zur Umsetzung
- Bestehende Views anpassen
  - CashDialog in Fragment umwandeln
  - Eventliste/ Eventansicht (Fragments) überarbeiten damit sie als ohne weiteres initalisert werden können
- TabLayout in bestehendes Layout integrieren
- Umsetzung für die Eventliste & globalen Ausgaben und für die Eventansicht & Eventausgaben

## Sonstiges

**Gesamtaufwand**: ~10h

 - UI-Überarbeitung
 - BugFixe
 - kurze Diskussionen/Gespräche über Umsetzbarkeit von Datenbankinhalten und Abfragen
 - FriendsActivity Logik
   - Freunde hinzufügen
 - Dauerhafte Benachrichtigung das Freundesliste leer ist und so wenig effektiv die Funktionweise
 - Datenbank zurücksetzen/ Dateb reset machen
 - Testen von Funktionen


## Projektdokumentation & Präsentationsvorbereitung

**Gesamtaufwand**: ~20h

- Technische Doku
- Präsentation
