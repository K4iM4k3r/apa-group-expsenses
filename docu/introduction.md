## Einstieg  
  
Die Geldsammler-Applikation wird verwendet, um es beliebig vielen (registrieren) Benutzern zu ermöglichen sich  
in sogenannten "Events" zusammenzufinden. In diesen Events können Ausgaben definiert werden.  
Die Ausgaben werden anschließend durch einen Algorithmus auf alle Gruppenmitglieder verteilt.   
  
# Anforderungen  
  
### Backend  
Das Projekt nutzt zwei unterschiedliche Backends:
 
* **Firebase**: Zuständig für die Verwaltung der Profildaten und den Emailverkehr  
* **Braintree:** Zuständig für die Abwicklung des Zahlungsverkehrs  
  
Eine Einbindung dieser Backends ist für die Funktionalität der Anwendung absolut notwendig.  
  
### Entwicklumgsumgebung  
Die Anforderungen für die Entwicklungsumgebung werden in der setup.md beschrieben.  

  
# Funktionalität  
Die Funktionalität der Anwendung wird zur besseren Übersicht
in dem Ordner **"functionality"** zusammengefasst. Dieser Ordner beinhaltet alle systemrelevanten Abschnitte 
der Implementierung. Folgende Abschnitte sind Bestandteil der Funktionalität:

- **Datenbank (database.md, livedata.md)**
- **Modelle (model.md)**
- **Views (activities.md, dialog.md, fragment.md)**
- **Services (service.md)**
- **Bezahlsystem (payment.md)**
  
 Die entsprechenden Dateien mit der Endung **.md** beinhalten die Technische Dokumentation der Funktionalität 
 und Implementierung der entsprechenden Abschnitte.

# Arbeitsaufwand

Der Projektaufwand wird im Ordner **"workload"** verteilt auf die entsprechenden Gruppenteilnehmer zusammengefasst. 
Alle Gruppenteilnehmer haben ihre Zeitinvestition in das Projekt dokumentiert und die einzelnen Arbeitsabschnitte erläutert.

## Grafische Darstellung
Die UML-Diagramme sind im Doku-Ordner UML enthalten.