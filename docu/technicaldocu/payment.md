## Grundlegender Ablauf

Die Interaktion für das Bezahlsystem erfolgt mithilfe der **CashFragment** Klasse.
Der Nutzer hat die Möglichkeit durch einen Klick auf den Bezahlen-Button (auch dargestellt als grüner Pfeil),
eine Bezahlung einzuleiten. Falls eine Bezahlung eingeleitet wurde, wird ausgehend von den oben gennanten Klassen
ein Intent mit putExtra an die PayActivity geschickt. Anschließend öffnet sich für den Nutzer die PayActivity.

In der PayActivity hat der Nutzer die Möglichkeit eine Zahlung durch Paypal oder eine Kreditkarte zu tätigen.
Sobald der Nutzer seine Zahlungsmethode eingegeben hat und auf bezahlen drückt, wird eine Anfrage an den 
Braintree Server gesendet. 

Die Anfrage enthält, neben den Zahlungsdaten des Nutzers auch einen öffentlichen und privaten Schlüssel und ein Token. 
Anhand dieser drei Parameter wird der Zahlungsverkehr verschlüsselt ist für jeden Nutzer einzigartig.
Nachdem die Anfrage vom Braintree Server verarbeitet wurde, sendet der Server ein Response-Token zurück and die 
PayActivity. Abhängig von dem Erfolg der Zahlung ist dieses Reponse-Token entweder "successful" oder "failed".

Falls es sich um eine fehlgeschlagene Transaktion handelt, wird dem Nutzer die Meldung "Transaktion fehlgeschlagen"
angezeigt. Bei einer erfolgreichen Transaktion wird dem Nutzer "Transaktion erfolgreich" angezeigt.

Bei einer erfolgreichen Transaktion wird die PayActivity mit finish() verlassen und der Nutzer landet wieder in der
Event Activity. Falls die Transaktion fehlschlägt ist der Nutzer weiterhin in der PayActivity und hat die 
Möglichkeit seine Zahlung erneut zu tätigen.

## Datenmodell

Die Anwendung verwendet als Datengrundlage das Framework **BraintreePayments**. 
Das Framwork wird in der Methode onActivityResult(), onBraintreeSubmit und sendPaymentDetails() genutzt.

Außerdem nutzt die PayActivity einen HTTP-Request und HTTP-Response.
Den Zahlungswert erhält die PayActivity anhand von putExtra im Intent der **CashFragment** Klasse. 

## Lifecyyle
onCreate() -> wenn die Activity über das CashFragment gestartet wird. **PayActivity gestartet**
sendPaymentDetails() -> finished() -> Wenn die Anfrage vom Braintree Server verbaitet wurde. **PayActivity beendet**
