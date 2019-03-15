# Activities

Frontend der Anwendung. zuständig für die Benutzerinteraktion.

	Beinhaltet:
-- BaseActivity  
-- EventActivity  
-- EventFormActivity  
-- FriendsActivity  
-- LoginActivity  
-- PayActivity  
-- PositionActivity  
-- PositionFormActivity  
-- ProfileActivity  

#### NavigationDrawer als BaseActivity
Alle Activities bis auf die Login Activity erben diese Klasse.

**Vorteile**
Bei jedem Start und Resume wird dort die Authentizität des Users überprüft und ob es sich noch um 
einen gültigen Account handelt. Einblendung von einer Benachrichtigung darüber, dass die Freundes/Kontaktliste leer ist 
und die App so nur eingeschränkt nutzbar ist.

#### EventActivity
Nutzt das Fragment *PositionEventListFragment* zur Darstellung der Eventliste.  
Hat einen Button zum hinzufügen neuer Events.

#### EventFormActivity
Nutzt das Layout *activity_event_form*, das Textfelder beinhaltet, die für die Erstellung von Events relevant sind.  

#### FriendsActivity
Nutzt das Layout *activity_friends*, um eine Freundesliste mittles ListView anzuzeigen inkl. Button zum hinzufügen eines neuen Freundes

#### Login/Create/Passwort Forgot 
Nutzt Firebase Authentifizierung  
Mit Email und Passwort Registrierung  
Nutzt Firebase Authentifizierung  
Mit Email und Passwort Registrierung  
Zusätzlich wird für den User in der Datenbank ein neues Dokument mit dem Namen der uid von Auth generiert und dort 
der nickname der einzigartig ist abgelegt in der Datenbank ein neues Dokument mit dem Namen der uid von Auth generiert 
und dort der nickname der einzigartig ist abgelegt

#### PositionActvity
Nutzt das Fragment *PositionEventListFragment* zur Darstellung der Positionsliste.  
Hat einen Button zum hinzufügen neuer Positionen.

#### PositionFormActivity
Nutzt das Layout *activity_position*, das Textfelder beinhaltet, die für die Erstellung von Positionen in einem Event relevant sind.

#### ProfileActivity
Ermöglicht die Bearbeitung des Profils über einen Edit-Button, der (fast) Textfelder aktiviert und somit bearbeitbar macht.  
Außerdem ist die Änderung des Profilbildes möglich.  
Mit einem Klick auf den Save-Button werden die Änderungen an den Server übermittelt.