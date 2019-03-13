## Einstieg  
  
Die Geldsammler-Applikation wird verwendet, um es beliebig vielen (registrieren) Benutzern zu ermöglichen sich  
in sogenannten "Events" zusammenzufinden. In diesen Events können Ausgaben definiert werden.  
Die Ausgaben werden anschließend durch einen Algorithmus auf alle Gruppenmitglieder verteilt.   

  
## Anforderungen  
  
- **Backend**  
Das Projekt nutzt zwei unterschiedliche Backends.  
Firebase Server: Zuständig für die Verwaltung der Profildaten und den Emailverkehr  
Braintree Server: Zuständig für die Abwicklung des Zahlungsverkehrs  
  
	Eine Einbindung dieser Backends ist für die Funktionalität der Anwendung absolut notwendig.  
  
- **Entwicklungsumgebung**
Die Anforderungen für die Entwicklungsumgebung werden in der setup.md beschrieben.  
  
## Funktionalität  
- **Activities-Ordner**  
Frontend der Anwendung. zuständig für die Benutzerinteraktion.

	Beinhaltet:
-- BaseActivity
-- EventActivity
-- EventFormActivity
-- FriendsActivity
-- LoginActivity
-- **PayActivity**
	>Logik für die Zahlungsabwicklung.

	-- PositionActivity
-- PositionFormActivity
-- ProfileActivity

  
## Technische Dokumentation  
Dieses Dokument dient nur der groben Übersicht.  
Die erwähnten Anwendungskomponenten werden in der Technischen Dokumentation (Ordner: technicaldocu)   
genauer beschrieben.

## Grafische Darstellung
Die UML-Diagramme sind im Doku-Ordner UML enthalten.