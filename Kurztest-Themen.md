## Zeitlinie
- 2005 Andorid von Google gekauft
- 2008 erste Android SDK (Api Level 1)
- Seit 2012 jährliche Updates

## Bestandteile der Android-Plattform
- Applications
- Application Frameworks
- Libraries
- Android Runtime
- Linux Kernel

- S. 10 (Build einer Anwendung)
    - ToDo (@David)

## Grundlagen - Andwenungen und Prozesse
- **Installation**: Anw. bekommt eine Linux Benutzer/Gruppen ID und eine Sandbox
- **Laufzeit**: Anw. bekommt einen isolierten Linux Prozess mit eigener VM.
**Anw. Berechtigungen**: 
    - *BIS Android 6*: Anw bekommt Berechtigungen die bei der Installation aktzeptiert werden.
    - *AB Android 6*: Anw. bekommt Berechtigungen die zur Laufzeit aktzeptiert werden

**Wichtige Anwendungskomponenten** 
- **Activities**: Sind Komponenten die das User-Interface und User-Interaktionen kontrollieren.
- **Service**: Keine Benutzerschnittstelle, Komponenten für Hintergrundaktivitäten
- **Broadcast Receiver**: Empfängt und verarbeitet Nachrichten 
- **Content Provider**: Regelt Datenzugriff und Datenströme zwischen Anwendungen 
- Foliensatz v2, S. 24-26: Erweitern einer Class um einen Contentprovider
- Foliensatz v2, S. 27: Registrierung eines Content-Provider
- Foliensatz v2, S. 29: Zugriff mit einem Content-Resolver

## Entwicklungsumgebung
- **build-tools**: Werkzeuge für den Build einer APP
- **extras** Support Libs/Repository,INTEL HAX, Google API usw.
- **platform-tools**: Werkzeuge zum analysieren/arbeiten mit Android. Werden mit jeder neuen Plattform aktualisiert und sind immer abwärtskompatibel.
- **platform**: Android SDK Plattformen (Libs/Ressourcen für einen API-Level)
- **sytem-images**: System-Images für den Emulator 
- **emulator**: Beeinhaltet Virtuelle Geräte zum testen der Android-Applikation.
- **tools**: SDK-Tools: SDK Manager, Dalvik Debug Monitor etc.

**Nachrichtenverarbeitung**
- Nachrichten: Benutzereingaben / Systemereignisse
- Prozesse besitzen einen Message-Loop, Nachrichten werden der Reihe nach durch einen Handler verarbeitet
    
## Tasks in Android
- Ein Task ist eine Android-Anwendung (aus Benutzersicht)
- Tasks können Activities aus unterschiedlichen Android-Anwendungen beeinhalten
- Tasks besitzen einen History Stack
- **History-Stack**: ein Stack auf dem die aufgerufenen Activities im Top-Down-Prinzip liegen. Wird eine Aktivity beendet, wird sie vom Stack genommen  

## Grundlagen Activity Lifecycle
Android System vewaltet Komponenten-Instanzen:
- Instanzen erzeugen/zerstören
- Lifecycle-Zustände ändern
- Instanzen informieren (Callback)

**Lifecycle-Zustände**:

    Event ->
    
    onCreate() -> created

    onStart() -> started (visible)

    onResume() -> resumed (visible + interactive)

    onPause() -> paused (partially visible)

    onStop() -> stopped (hidden)

    onDestroy() -> destroyed

**Sonderfälle**

    paused -> onResume() -> resumed

    stopped -> onRestart() -> onStart() ->  started

- S. 57-62 (Prozesshierachie)


## ap-v2
- S. 8-20 (Room Quellcode ansehen)

## ap-v3
- S. 6 (Nebenläufigkeit)
- S. 19 (Foregroundservice)

# NOTES: 
## Intents:
- enthält ein Event, das ausgelöst wurde oder beshreibt eine Aktion die ausgeführt werden soll
 
- **explizit**: Empfänger zur Laufzeit bekannt
    Bsp: 

- **implizit**: Es werden eine Aktion und Daten angegeben. Empfängerkomponente wird zur Laufzeit ermittelt.
- Bestandteile: Siehe Folie 2, S. 16-18
    - Szenario 1: Aktivity gefunden und gestartet
    - Szenario 2: mehrere passende Aktivities gefunden
    - Szenario 3: keine Aktivities gefunden
        Bsp: Siehe Folie 2, S.11

## Prozessmanagement
- Start einer Anwendung: 
- Standardfall: in genau einem linuxprozess ausgeführt

## Prozess-Stresstest
- isUserAMonkey()-Methode

## Room
- ein robusterer SQL-Datenbankzugriff
- besteht aus der Room-Database, Data access objects und Entities

## Nebenläufigkeit Threading
- Threadpool
    - sofort und nach bestem Bemühen

- Foreground Service
    - sofort und garantiert

- WorkManager
    - garantiert aber aufschiebbar 
    - aufschiebbare Hintergrundaufgaben (async task)
    - ausführung durch constraints geregelt
