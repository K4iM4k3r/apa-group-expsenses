
| Tätigkeit                 | Aufwand (real)  | Aufwand (geschätzt) |  
|---------------------------|:---------------:|:-------------------:|  
|Projektkoordination           | 30h            | 30h                 |  
|Recherche Bezahlsystem        | 10h            | 10h                 |  
|Implementierung Bezahlsystem  | 30h            | 20                  |  
|Recherche Notification        | 10h            | 10h                 |  
|Implementierung Notification  | 25h            | 20                  |  
|Testing&Bugfixing             | 10h            | 10                 |  
|Doku/Präsentation             | 15h            | 20                  |  
|**Summe** | **~145h** | **135<sup>\*</sup>**|
  
  
## Projektkoordination  
  
Allgemeine Kommunikation innerhalb der Gruppe durch Meetings und Messenger-Gruppen.  
Meetings haben wöchentlich stattgefunden und waren maßgeblich für die Koordination des  
Projektfortschritts. Im Schnitt hatten die Meetings eine Dauer von 1-2h, abhängig von der Komplexität.  
Nach den Meetings wurden neue Issues im GIT-Repository erstellt oder Issues geschlossen.  
  
## Recherche Bezahlsystem.  
Um das Bezahlsystem umzusetzen, war es notwendig sich die nötigen Kompetenzen anzueignen.  
In der Vergangenheit musste ich noch keine vergleichbare Funktion implementieren, daher war der   
Aufwand der Recherche relativ hoch. Anfangs war ich der Meinung, das Bezahlsystem durch einen HTML-Tunnel  
innerhalb der Applikation durchzuführen, dies hätte aber dazu geführt das die Interaktion mehrheitlich außerhalb  
der Applikation stattfinden würde. In diesem Fall wäre das Bezahlformular (Eingabe der Zahlungsdaten) auf einer  
HTML-Seite angezeigt worden, welche von der Anwendung durch einen Listener geöffnet wird.  
  
Diese Implementation hatte aber auch einen Vorteil, dadurch hätte man das Firebase Backend nutzen können und  
die Zahlungsabwicklung anhand einer **Firebase Cloud-Function** umsetzen können. Dadurch hätte man auch direkt von  
dem Firebase-Backend die Transferdaten analysieren können und hätte dementsprechend eine kompaktere Datenverwaltung.  
  
## Implementierung Bezahlsystem  
### Firebase  
Anfänglich habe ich versucht die Variante mit **Firebase Cloud Functions** zu implementieren.  
Eine HTML-Seite wurde erstellt und mit dem Firebase Backend direkt als Hosting-Seite verknüpft, die Logik der HTML-Seite  
wurde durch eine Cloud-Function (**charge function**) gewährleistet. Leider war es nicht möglich eine Anfrage an einen  
Bezahldienstleister zu senden. Dies lag daran das Firebase seit kurzem im Standard Gratisabo keine Cloud Functions  
erlaubt die auf Drittanbieter (outside source) referenzieren. Dementsprechend musste diese Implementierung anschließend  
komplett verworfen werden.  
  
### Braintree Payments  
Aufgrund des Rückschlags bei der Firebase Implementierung, war ich dazu gezwungen auf eine Alternativlösung umzusteigen.  
Die Wahl viel hier auf eine Implementierung des **Braintree Payments Framework**. Das Framework wurde der Applikation.build  
hinzugefügt. Dadurch war es mir möglich den gesamten Bezahlungsablauf (bis auf die Anfrage an den Bezahldienstleister) in   der Applikation zu implemtieren. Entsprechend wurde eine PayActivity erstellt, welche wiederrum durch onClick Listener  
im CashFragment geöffnet wird. In der PayActivity wurde die Bezahlung anhand von HTTP-Requests und Reponses und  
Braintree Payments Funktionen durchgeführt. Ein entsprechender **Braintree Server** als Backend war notwendig und dient  
zur Verwaltung und Monitoring des Bezahlsystems.  
  
Ein großer Vorteil dieser Implementierung war die Möglichkeit auf sogennante "Sandbox" Payments zurückzugreifen.  
Dies ermöglicht dem Entwickler Bezahlungen mit Testkreditkarten (die von Braintree zur Verfügung gestellt werden) und  
Testpaypal-Transaktionen durchzuführen. Diese Zahlungen werden vom Braintree Backend im Bezug auf das Monitoring wie  
echte Zahlungen behandelt, es findet in der Realität aber keine Geldbewegung statt.  
  
## Recherche Notifications  
Bei der Rechereche musste ich mich zunächst damit beschäftigen welche Implementierungsvarianten zur Verfügung stehen.  
Android bietet unterschiedliche Möglichkeiten Bestandteile einer Anwendung als Hintergrundoperation weiterlaufen zu lassen.  
Eine Möglichkeit dazu ist der Foreground-Service, welchen ich relativ schnell als Option verworfen habe. Dieser Service  
ist konstant in der Benachrichtigungsleiste sichtbar und signalisiert damit das er aktiv ist. Er ist somit also eher für  
Services wie z.b. einen Musik-Player geeignet.  
  
Dementsprechend habe ich mich stattdessen dazu entschlossenen einen normalen Service zu implementieren. Die Service-Klasse  
von Android bietet alle Eigenschaften die ich benötigt habe, Nachteil hierbei ist das Android seid Android Oreo im  
Hintergrund laufende Services nach einer bestimmten Zeit terminierten um Ressourcen freizugeben. Entsprechende   
Optimierungsmöglichkeiten um diesen Service dauerhaft präsent zu gestalten sind also für die Zukunft möglich.  
  
## Implementierung Notificiations  
Die Notifcations wurden in einer eigenen Klasse (**NotificationService**) umgesetzt. Die Klasse nutzt hierzu die Daten  
der LiveData (package: **livedata**). Falls der Nutzer die Applikation schließt oder minimiert wird der aktuelle  
Zustand der Applikation im Notification-Service festgehalten. Die NotificationService Klasse reagiert dabei auf den Zustand  
der *App* Klasse. Die **App** Klasse wurde um die Methoden onActivityStarted (= Service angehalten) und onActivityStopped  
(= Service gestartet) erweitert. Die Notification-Service Klasse ist somit nur aktiv wenn die Anwendung gestoppt wurde.  
  
**Umgesetzt wurden die folgenden Notifications:**
- Der Nutzer wurde einem Event hinzugefügt.  
- Einem Event wurde um eine neue Position erweitert.  
- Der Nutzer hat eine Zahlung bei einer seiner Positionen erhalten.  
- Der Nutzer wurde als Freund hinzugefügt  
- Der Aktionszeitraum eines Events endet in 3,2 oder 1 Tag(en).  
  
Alle Notifications befinden sich in der NotificationService Klasse.  
  
## Testing&Bugfixing  
Durch die Benutzung der oben genannten Features kam es vereinzelt zu Fehlern die während dem Implementierungsprozess   
nicht aufgefallen sind. Um diese Fehler zu beseitigen und auf Fehleranfälligkeit zu testen, wurde entsprechende Zeit  
investiert und dokumentiert.  
  
## Doku&Präsentation  
Nach dem erfolgreichen Abschluss aller Features wurde die Softwaredokumentation erstellt und die Präsentation vorbereitet.  
Die Meetings haben sich gegen Ende dementsprechend primär mit dem Feinschliff der Softwaredokumentation beschäftigt  
und wir haben uns als Gruppe gemeinsam auf den Abgabetermin vorbereitet.