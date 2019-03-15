## Grundlegender Ablauf  
  
Alle Notifications sind in der NotificationsService.class definiert.  
Hierbei handelt es sich um eine Android-Service Klasse, sie ist **bound** an die Applikation (App.Java).  

Solange sich die Anwendung im Speicher befindet und nicht komplett terminiert ist, liefert die  
Service-Klasse unterschiedliche Notifications in Form von Android Popup-Notifications mit zusätzlichem Sound.  

Dies wird durch ein IMPORTANCE_LEVEL von **high** erreicht.   
  
Falls die Anwendung komplett terminiert wird, wird der Service sobald die Anwendung wieder gestartet wird  
aktiviert und gleicht sich mit seinem zuletzt bekannten LiveData Datensatz ab. Kommt es zu Unterscheidungen  
mit den vom NotificationService zwischengespeicherten Datensatz, führt der Service nachträglich 
die Notifications aus, um den Nutzer über Änderungen zu informieren.  
  
Der NotificationService ist während der Laufzeit der Applikation aktiv. Entsprechende Zustandsänderungen werden   
dem aktiven Nutzer unabhängig davon angezeigt ob er sich in der Anwendung befindet oder nicht.  
  
**Die folgenden Thematiken sind durch Notifications abgedeckt:**  
- Der Nutzer wurde einem neuen Event hinzugefügt  
- Eine Position wurde in einem Event hinzugefügt in dem der Nutzer ein Teilnehmer ist  
- Der Nutzer wurde von einer anderen Person als Freund hinzugefügt  
- Eine offene Position des Nutzers wurde zurückgezahlt  
  
Die vier Notifications sind in der NotificationService.class in Notificationchannels zusammengefasst.  

Jede Notification hat abhängig vom Typ einen anderen Request-Code. Es gibt insgesamt 4 Request-Codes.  

Notifications vom selben Typ überschreiben sich, indem sie den gleichen Request-Code haben.  Es ist also maximal möglich das der Nutzer 4 unterschiedliche aktive Notifications erhält.  
  
  
  
## Datenmodell  
  
Die Notifications werden mit dem Android-eigenen Builder generiert.   

- **.setSmallIcon** 
>**Das Symbol der Notification in der Android-Statusbar**  
- **.setContentTitle** 
>**Der Titel der Notification**  
- **.setContentText** 
>**Die Nachricht der Notification**  
- **.setPriority(NotificationCompat.PRIORITY_MAX)** 
>**Priorität der Notification, bei API kleiner ANDROID OREO**  
- .setCategory(NotificationCompat.CATEGORY_MESSAGE)
>**ANDROID_OS Kategorie der Notification**  
- **.setContentIntent(pendingIntent)** 
>**Intent beim klicken auf die Notification -> entsprechende Activity**  
- **.setAutoCancel(true)** 
>**Automatisches schließen der Notification nach einmaligem Klick**  
- **.build();** 
>**Bau der Notification mit allen oben genannten Bausteinen**  

____________________________________________________________________________

**Datenabgleich**

Der LiveData Datensatz wird in den Variablen **oldEventList** und **oldFriendList** abgespeichert. 
Zustandsänderungen werden durch Unterschiede  im Listenindex erkannt und entsprechend auf  
die richtige Objekt-ID referenziert.
  
## Lifecyyle  
onCreate() **->** Wenn die Applikation gestartet wird.  
onDestroy() **->** Wenn die Applikation terminiert ist.