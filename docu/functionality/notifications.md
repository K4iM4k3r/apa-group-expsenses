## Grundlegender Ablauf

Alle Notifications sind in der NotificationsService.class definiert.


## Datenmodell

Die Anwendung verwendet als Datengrundlage das Framework **BraintreePayments**. 
Das Framwork wird in der Methode onActivityResult(), onBraintreeSubmit und sendPaymentDetails() genutzt.

Außerdem nutzt die PayActivity einen HTTP-Request und HTTP-Response.
Den Zahlungswert erhält die PayActivity anhand von putExtra im Intent der **CashFragment** Klasse. 

## Lifecyyle
onCreate() -> wenn die Activity über das CashFragment gestartet wird. **PayActivity gestartet**
sendPaymentDetails() -> finished() -> Wenn die Anfrage vom Braintree Server verbaitet wurde. **PayActivity beendet**