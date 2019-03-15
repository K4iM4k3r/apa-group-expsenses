# Activities

Frontend der Anwendung.  
Zuständig für die Benutzerinteraktion.

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
Alle Activities bis auf die *LoginActivity* erben diese Klasse.

**Vorteile**  
Bei jedem Start und Resume wird dort die Authentizität des Users überprüft und ob es sich noch um einen gültigen Account handelt.  
Einblendung von einer Benachrichtigung darüber, dass die Freundes/Kontaktliste leer ist und die App so nur eingeschränkt nutzbar ist.

#### EventActivity
**Layout:** *R.layout.activity_event.xml*  
Nutzt das Fragment *PositionEventListFragment* zur Darstellung der Eventliste.  
Hat einen Button zum hinzufügen neuer Events.

#### EventFormActivity
**Layout:** *R.layout.activity_event_form.xml*  
Nutzt das obige Layout, das Textfelder beinhaltet, die für die Erstellung von Events relevant sind.  

#### FriendsActivity
**Layout:** *R.layout.activity_friends.xml*  
Nutzt das obige Layout, um eine Freundesliste mittles ListView anzuzeigen inkl. Button zum hinzufügen eines neuen Freundes

#### Login/Create/Passwort Forgot 
-> nutzt Firebase Authentifizierung  
-> mit Email und Passwort Registrierung  
Zusätzlich wird in der Datenbank ein neues Dokument mit dem Namen der *uid* von Auth generiert und dort der *nickname*, der einzigartig ist, abgelegt

#### PositionActvity
**Layout:** *R.layout.activity_position.xml*  
Nutzt das Fragment *PositionEventListFragment* zur Darstellung der Positionsliste.  
Hat einen Button zum hinzufügen neuer Positionen.

#### PositionFormActivity
**Layout:** *R.layout.activity_position_form.xml*  
Nutzt das obige Layout, das Textfelder beinhaltet, die für die Erstellung von Positionen in einem Event relevant sind.

#### ProfileActivity
**Layout:** *R.layout.activity_profile.xml*  
Ermöglicht die Bearbeitung des Profils über einen Edit-Button, der (fast) Textfelder aktiviert und somit bearbeitbar macht.  
Außerdem ist die Änderung des Profilbildes möglich.  
Mit einem Klick auf den Save-Button werden die Änderungen an den Server übermittelt.

# Dialogs
Frontend-Gimmick der Anwendung.  
Zuständig für die Darstellung von Infos sowie kürzere Benutzerinteraktionen.

	Beinhaltet:
-- EventInfoDialog 
-- InviteDialog  
-- PositionInfoDialog  
-- ProfileInfoDialog  

#### EventInfoDialog
**Layout:** *R.layout.dialog_event_view.xml*  
Zeigt folgende Infos über ein bestimmtes Event an:
- Name
- Info
- Ersteller + Datum
- eigene Ausgaben im Event + Anteil an den Gesamtausgaben des Events

Als **Ersteller** des Events wird ein zusätzlicher Button eingeblendet, der die Bearbeitung von Event-Info ermöglicht.  

#### InviteDialog
**Layout:** *R.layout.dialog_invite_view.xml*  
Bietet **drei** Möglichkeiten, eine Einladung zu einem erstellten Event innerhalb der App zu versenden:
- per E-Mail
- per WhatsApp
- per copy to clipboard

Bei Auswahl einer Möglichkeit wird der Dialog wieder geschlossen.

#### PositonInfoDialog
**Layout:** *R.layout.dialog_position_view.xml*  
Zeigt folgende Infos über eine bestimmte Position an:
- Name
- Info
- Ersteller + Datum
- Deine Forderungen/Schulden

Als **Ersteller** der Position werden **zwei** zusätzliche Buttons eingeblendet, die die
- Bearbeitung von Positions-Info
- Bearbeitung des Positions-Betrags

ermöglichen.

#### ProfileInfoDialog
**Layout:** *R.layout.dialog_profile_view.xml*  
Zeigt das für alle sichtbare, öffentliche Profil des jeweiligen Nutzers an.  
Beinhaltet (von oben nach unten):
- Profilbild (optional)
- Vor + Nachname (optional)
- Nickname
- Über mich/ Info
- Beitritt (nicht editierbar)

# Fragments
Zur vermeidung von Redundanzen in der Anwendung.  
Zuständig für die Darstellung wiederkehrender Muster.

	Beinhaltet:
	
-- PositionEventListFragment
-- UserListDialogFragment
-- CashFragment

#### PositionEventListFragment
**Layout:** *R.layout.fragment_object_list.xml*
Ein *object* kann sowohl ein Event als auch eine Position sein, die mittels dieses Fragments als ListView dargestellt werden können.  
Bei Klick auf ein item in der Liste wird der entsprechende InfoDialog aufgerufen (*EventInfoDialog* oder *PositionInfoDialog*).  
Bei noch genauerem Klick auf den Ersteller in einem item in der Liste wird der *ProfileInfoDialog* des Erstellers aufgerufen.  
Es exisitiert außerdem ein Header, der fix über der Liste ist und einen Gesamtbetrag anzeigt, abhängig von Position oder Event-Liste.  
In der Event-Liste werden links zusätzlich *ImageView*´über den Status des Events eingeblendet.

#### UserListDialogFragment
**Layout:** *R.layout.fragment_user_list.xml*
Zeigt eine ListView aus User.  
Es existieren zwei Umstände, unter welchen dieses Fragment aufgerufen wird:
1. create_event
2. edit_event

Abhängig von diesen zwei Zuständen ist eine unterschiedliche Ansicht dieses Dialogs implementiert. Teilweise fungiert dieser Dialog als Picker, teils aber auch nur als reine Auflistung der Nutzer.

#### CashFragment
**Layout:** *R.layout.fragment_cash_check.xml*
Hier wird die Bilanz zwischen uns (dem *App.CurrentUser*) und anderen Event-Freunden dargestellt, zwischen denen entweder Forderungen oder Schulden existieren können.  
Grüner Pfeil -> Wir haben Forderungen bei der anderen Person  
Roter Pfeil -> Wir haben Schulden bei der anderen Person  
Je nach Farbe und Richtung des Pfeils kann bei Klick auf den Pfeil unterschiedliche Aktionen hervorgerufen werden:  
Grüner Pfeil -> Zahlungserinnerung oder Barzahlung hinzufügen  
Roter Pfeil -> Schulden per Online-Banking bezahlen







