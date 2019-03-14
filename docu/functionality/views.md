# Activities

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

## NavigationDrawer als BaseActivity
Alle Activities bis auf die Login Activity erben diese Klasse

**Vorteile**
Bei jeden Start und Resume wird dort dir Authentizität des Users überprüft ob es sich noch um 
einen gültigen Account handelt. Einblendung von einer Benachrichtigung darüber dass die Freundes/Kontaktliste leer ist 
und die App so nur eingeschränkt nutzbar ist 


## Login/Create/Passwort Forgot 
Nutz Firebase Authentifizierung 
Mit Email und Passwort Registrierung 
Zusätzlich wird für den User Login/Create/Passwort Forgot 
Nutz Firebase Authentifizierung 
Mit Email und Passwort Registrierung 
Zusätzlich wird für den User in der Datenbank ein neues Dokument mit dem Namen der uid von Auth generiert und dort 
der nickname der einzigartig ist abgelegtin der Datenbank ein neues Dokument mit dem Namen der uid von Auth generiert 
und dort der nickname der einzigartig ist abgelegt