# Technische Doku

## Datenbank

- Firestore von Firebase wird genutzt
  - Realtime Datenbank
- Zur einfacheren Kommunikation mit der CloudDatabase gibt es einen DatabaseHandler
  - zentrale Stelle für Datenbankoperationen
  - wird in App genutzt um Queries und Änderungen an der Datenbank zu tätigen
  - Alle Methoden sind statisch und können ohne Objektinitalisierung genutzt werden
  - Alle Methoden besitzen JavaDoc Kommentare
- Zur Verwaltung werden Modelklassen genutzt, die so angepasst sind das sie ohne Probleme in der Datenbank abgelegt werden können
  - d.h. keine Sets, HashMap oder Date Objekte, da sie nicht unterstützt werden, Date Objekte werden deshalb als long gespeichert und in der App ein Dateobjekt daraus erstellt
  - Models:
    - User
    - Event
    - Position
  - zur jedem Model ebenfalls JavaDoc Dateien

###### Probleme :
Keine SQL Datenbank sondern eine Objektorientierte die mit Collections und Dokumenten arbeiten
Or Queries sind nicht so ohne weiteres möglich führt zu längeren Abfragezeiten
Negative Bedingungen bei Abfragen bei Arrays nicht unterstützt

### Queries
Es gab mehrere Iterationen der Anfragen (Queries) 
- erst gab es nur einmalige Antworten in einem async Callback (jetzt -> deprecated)
- Jetzt Anwort auf Queries als LiveDataObjekt 
  - um auf stetige Änderungen an den ausgewählten Daten zu reagieren zu können
  - Ausnahme ist die Methode **_getAllFriendsOfUser_** die kein LiveDataObjekt zurückliefert sonder nur einmalig eine Liste mit Usern im Callback liefert 

######  LiveDataObjekt
Eigene LiveDataKlassen:
- EventListLiveData
- EventLiveData
- UserListLiveDate
- UserLiveData

Grob aufgeteilt gibt es Listen LiveDataObjekte und LiveDataObjekte die nur ein einzelenes Objekte beinhalten
- Der Konsruktor erwartet die Query die beobachtet werden soll
- Intern wird ein Listener gesetzt der immer bei Änderungen bzw. initial einmal die Daten zurückliefert
- Generalisierung mit Generics leider nicht möglich da in den Listener die jeweiligen ModelKlassen benötigt werden