## Grundliegender Ablauf

Meldet sich ein Nutzer an so wird sein User aus der DB angefragt und in der App unter App.CurrentUser gespeichert.
Hat er noch kein Konto, so kann er ein solches erstellen. Nach Bestätigung seiner E-Mail Adresse wird sein Nutzer der
DB hinzugefügt und ebenfalls in App.CurrentUser gespeichert.

Die App betrachtet im Folgenden sämtliche Aktionen aus der Sicht des App.CurrentUser.

Die MainActivity (EventActivity) registriert ein LiveData Object zur Überwachung der dem Nutzer zugewiesenen Veranstaltungen.
Hierdurch wird eine Realzeit Synchronisation ermöglicht. Gleichzeitig wird bei Änderungen die Bilanz aktualisiert, so dass der
aktuelle Ausgabenstand stets nachvollziehbar ist.

Ein neues Event kann via FAB erstellt werden. Mit klick auf das neu hinzugefügte Event gelangt der Nutzer zur Liste der
bereits getätigten Ausgaben (PositionActivity). Hier können beispielsweise Nutzer hinzugefügt, Ausgaben verwaltet oder 
die Event Informationen angepasst werden.

Die Navigation durch die App sollte weitestgehend selbsterklärend sein.


## Datenmodell

Die Anwendung verwendet als Datengrundlage die Klassen **Event** und **User**.  

Hierbei wird für jeden Benutzer der App beim Login bzw. nach der Registrierung ein User angelegt.
Dieser koexistiert neben dem, von Firebase zur Authentifizierung verwendeten FirebaseUser. Die beiden
teilen sich hierbei die gleiche NutzerId, welche im Folgenden zur Verwaltung der Ausgaben verwendet wird.

Die Event Klasse dient als DataHolder für angelegte Veranstaltungen und implementiert zusätzlich einige
Methoden zur Ausgabenverwaltung. Ein Event befindet sich hierbei stets in einen festen LifecycleState.


## Lifecyyle

Der aktuelle LifecycleState des Events bestimmt sich aus dem Anfangs- und Enddatum. Dazu kommt eine Review Phase, welche 
nach dem Enddatum folgt. Aus dem Enddatum und der Reviewphase ergibt sich der DeadlineDay, welcher als Schlussstrich
der Veranstaltung zu sehen ist. Die Auswirkung der Lifecycles sind der folgenden Tabelle zu entnehmen:


|State      |Wann                                |Nutzer hinzufügen|Position hinzufügen|Bezahlen|Gruppe verlassen<sup>\*1</sup>|Gruppe Löschen<sup>\*2</sup>|
|:---------:|-----------------------------------:|:---------------:|:-----------------:|:------:|:----------------------------:|:--------------------------:|
|UPCOMING   |Anfangsdatum in Zukunft             |x                |x                  |        | x<sup>\*3</sup>              |x<sup>\*3</sup>             |
|LIVE       |Zwischen Beginn- & Enddatum         |x                |x                  |        |                              |                            |
|LOCKED     |Zwischen Ende & DeadlineDay         |                 |x                  |x       | x<sup>\*4</sup>              |                            |
|CLOSED     |Nach DeadlineDay                    |                 |                   |        | x                            | x                          |
|ERROR      |bei invalidem Datum <sup>\*5</sup>  |                 |                   |        | x                            | x                          |



<sup>\*1</sup> nur als Teilnehmner  
<sup>\*2</sup> nur als Ersteller  
<sup>\*3</sup> solange keine offenen Transaktionen vorhanden  
<sup>\*4</sup> hier wird man nur ausgeblendet. Sollten neue Positionen hinzugeüft werden wird man wieder aktiv gesetzt.  
<sup>\*5</sup> solte nie vorkommen  


## Expense-Management

Die Ausgaben ergeben sich aus dem aktuellen Nutzer (App.CurrentUser), der Gesamtanzahl der Eventmitglieder sowie dem
Gesamtwert der angelegten Position. Hierzu stellt die Position Klasse Methoden zur Berechnung der Schulden eines einzelnen Users zur Verfügung.
Diese werden hauptsächlich von der Eventklasse verwendet, erlauben aber eine saubere Trennung von Positionen und Events.
Weitergehend ist es möglich sich innerhalb einer Position in eine Liste (peopleThatDontHaveToPay) einzutragen. Dies geschieht zB sobald ein Nutzer
seine Schulden an der Position begleicht, kann aber auch dazu verwendet werden Personen vom Bezahlen einzelner Positionen auszuschließen. 
Dies wäre besipielsweise von Interesse, wenn der Gruppe neue Mitglieder hinzugefügt werden, welche an den vorherigen Positionen nicht beteiligt werden 
sollen.