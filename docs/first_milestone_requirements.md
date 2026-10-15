# Haushaltsapp – Anforderungsdokument

Oct 15, 2026 · @Massimo

## Ziel und Rahmen

Die App organisiert einen gemeinsamen Haushalt an einem Ort: Mitglieder, Finanzen und Aufgaben mit Erinnerungen.

- **Zielgruppe:** der eigene Haushalt, wenige bekannte Personen. Kein öffentlicher Release im Play Store.
- **Plattform:** Android zuerst. Gebaut mit Kotlin Multiplatform, damit eine iOS-App später die gleiche Logik nutzen kann.
- **Backend:** Spring Boot mit Kotlin, selbst gehostet auf einem Server zuhause.
- **Umfang Version 1:** Mitgliederverwaltung, Finanzen, Tasks mit Reminder.

Jede Anforderung hat eine ID (B = Benutzer, F = Finanzen, T = Tasks), damit sie in Tickets und Tests referenziert werden kann.

## Benutzer, Haushalt und Rollen

Jede Person hat ein eigenes Konto und gehört zu genau einem Haushalt. Es gibt zwei Rollen: Admin und Mitglied.

| ID | Anforderung |
| --- | --- |
| B-01 | Registrierung mit Name, E-Mail und Passwort. |
| B-02 | Login mit E-Mail und Passwort. Die Sitzung bleibt bis zum Logout bestehen (Refresh-Token). |
| B-03 | Nach der Registrierung gründet die Person einen neuen Haushalt (und wird Admin) oder tritt per Einladungscode einem bestehenden bei. |
| B-04 | Ein User ist Mitglied in genau einem Haushalt. |
| B-05 | Ein Admin erzeugt einen Einladungscode, als Text und als QR-Code. Der Code ist befristet und nur einmal verwendbar. |
| B-06 | Ein Admin kann Mitglieder entfernen. Danach hat die Person keinen Zugriff mehr auf die Daten des Haushalts. |
| B-07 | Ein Admin kann andere Mitglieder zu Admins machen und Admin-Rechte entziehen. Es bleibt immer mindestens ein Admin. |
| B-08 | Ein Mitglied kann den Haushalt selbst verlassen. |
| B-09 | Jeder User kann sein Passwort ändern. |

**Rechte nach Rolle**

| Aktion | Admin | Mitglied |
| --- | --- | --- |
| Mitglieder einladen, entfernen, Rollen ändern | ja | nein |
| Kategorien und Budgets verwalten | ja | nein |
| Gemeinsame Buchungen erfassen | ja | ja |
| Buchungen bearbeiten und löschen | alle | nur eigene |
| Private Buchungen anderer sehen | nein | nein |
| Tasks erstellen, bearbeiten, abhaken | ja | ja |

## Finanzen

Der Haushalt erfasst Einnahmen und Ausgaben in Kategorien, setzt Monatsbudgets und sieht Auswertungen. Ausgewertet wird immer in CHF, auch wenn in Fremdwährung bezahlt wurde.

| ID | Anforderung |
| --- | --- |
| F-01 | Buchung erfassen: Typ (Einnahme oder Ausgabe), Betrag, Währung, Datum, Kategorie, Notiz, optional als „privat“ markiert. |
| F-02 | Buchungen bearbeiten und löschen (Rechte siehe Rollen). |
| F-03 | Buchungsliste mit Filter nach Monat, Kategorie, Typ und Person. |
| F-04 | Private Buchungen sieht nur, wer sie erfasst hat. Sie zählen nicht in Budgets und Auswertungen des Haushalts. |
| F-05 | Beim Gründen eines Haushalts werden Standardkategorien angelegt. Ein Admin kann Kategorien hinzufügen, umbenennen und archivieren. Archivierte Kategorien bleiben an alten Buchungen erhalten. |
| F-06 | Ein Admin legt pro Ausgabenkategorie ein Monatsbudget in CHF fest. |
| F-07 | Bei 80 % und bei 100 % eines Budgets erhalten alle Mitglieder eine Push-Nachricht, je Schwelle einmal pro Kategorie und Monat. |
| F-08 | Die App zeigt pro Budget einen Fortschrittsbalken (verbraucht / Budget). |
| F-09 | Wiederkehrende Buchungen werden als Vorlage gespeichert: Betrag, Kategorie, Intervall (wöchentlich, monatlich, jährlich), Stichtag. Am Stichtag kommt eine Push-Erinnerung. Die Buchung entsteht erst, wenn sie bestätigt wird; der Betrag kann vorher angepasst werden. |
| F-10 | Fremdwährungen: Der Betrag wird mit dem EZB-Referenzkurs des Buchungsdatums in CHF umgerechnet. Gespeichert werden Originalbetrag, Währung, Kurs und CHF-Betrag. An Tagen ohne Kurs (Wochenende, Feiertag) gilt der letzte verfügbare. |
| F-11 | Monatsübersicht: Summe Einnahmen, Summe Ausgaben, Saldo. |
| F-12 | Diagramme: Ausgaben nach Kategorie für einen Monat und Verlauf von Einnahmen und Ausgaben über 12 Monate. |

**Standardkategorien**

- Ausgaben: Wohnen, Lebensmittel, Haushalt, Mobilität, Versicherungen, Gesundheit, Freizeit, Ferien, Diverses
- Einnahmen: Lohn, Diverses

## Tasks und Reminder

Tasks haben eine Fälligkeit, Priorität, Kategorie und optional eine Checkliste. Sie können wiederkehren, reihum rotieren und mehreren Personen zugewiesen sein. Reminder werden relativ zur Fälligkeit gesetzt.

| ID | Anforderung |
| --- | --- |
| T-01 | Task erstellen: Titel, Beschreibung, Fälligkeit (Datum, optional Uhrzeit), Priorität (Hoch, Mittel, Tief), Kategorie, Zuweisung, Reminder. |
| T-02 | Zuweisung an eine oder mehrere Personen. Ohne Zuweisung gilt der Task für alle Mitglieder. |
| T-03 | Ein Task ist erledigt, sobald eine zugewiesene Person ihn abhakt. Gespeichert wird, wer ihn wann erledigt hat. |
| T-04 | Checkliste: Unterpunkte können einzeln abgehakt werden. Der Task kann auch mit offenen Unterpunkten erledigt werden. |
| T-05 | Wiederkehrende Tasks: täglich, wöchentlich (Wochentage wählbar), monatlich, alle X Tage oder Wochen. Jede Wiederholung ist eine eigene Instanz mit eigenem Erledigt-Status. |
| T-06 | Rotation: Bei wiederkehrenden Tasks kann eine Reihenfolge von Personen festgelegt werden. Jede Instanz geht an die nächste Person. Mit Rotation hat jede Instanz genau eine zugewiesene Person. |
| T-07 | Reminder relativ zur Fälligkeit: zur Fälligkeit, 15 Minuten, 1 Stunde, 1 Tag oder 1 Woche vorher. Ein Reminder pro Task. |
| T-08 | Der Reminder geht an die zugewiesenen Personen, ohne Zuweisung an alle Mitglieder. Bei erledigten Tasks wird kein Reminder mehr gesendet. |
| T-09 | Ansichten: „Meine Tasks“, „Alle“, „Erledigt“. Sortiert nach Fälligkeit, filterbar nach Kategorie und Priorität. |
| T-10 | Überfällige Tasks werden hervorgehoben. |
| T-11 | Standard-Taskkategorien: Putzen, Einkauf, Wäsche, Administratives, Garten, Reparaturen. Ein Admin kann sie anpassen (wie F-05). |

## Benachrichtigungen

Alle Push-Nachrichten verschickt der Server über Firebase Cloud Messaging. Sie kommen auch an, wenn die App geschlossen ist.

| Ereignis | Empfänger | Zeitpunkt |
| --- | --- | --- |
| Task-Reminder (T-07) | Zugewiesene Personen, sonst alle | Wie am Task eingestellt |
| Wiederkehrende Buchung fällig (F-09) | Person, die die Vorlage erstellt hat | Am Stichtag |
| Budget zu 80 % verbraucht (F-07) | Alle Mitglieder | Sobald die Schwelle erreicht ist |
| Budget zu 100 % verbraucht (F-07) | Alle Mitglieder | Sobald die Schwelle erreicht ist |

Ab Android 13 muss die App die Benachrichtigungs-Berechtigung beim ersten Start anfragen. Ein Tipp auf eine Push-Nachricht öffnet direkt den betroffenen Task, die Buchungsvorlage oder das Budget.

## Nicht-funktionale Anforderungen

Der Server ist über den Cloudflare Tunnel öffentlich erreichbar. Deshalb ist Sicherheit trotz kleinem Nutzerkreis Pflicht.

| ID | Bereich | Anforderung |
| --- | --- | --- |
| N-01 | Sicherheit | Kommunikation nur über HTTPS (übernimmt der Cloudflare Tunnel). Am Router wird kein Port geöffnet. |
| N-02 | Sicherheit | Passwörter werden mit BCrypt oder Argon2 gehasht gespeichert. |
| N-03 | Sicherheit | Kurzlebige Access-Tokens (JWT) plus Refresh-Token. Entfernte Mitglieder verlieren sofort den Zugriff. |
| N-04 | Sicherheit | Begrenzung der Login-Versuche gegen Passwort-Raten. |
| N-05 | Sicherheit | Jede Server-Abfrage prüft, dass die Daten zum Haushalt des Users gehören. |
| N-06 | Verbindung | Die App funktioniert nur online. Ohne Netz zeigt sie eine verständliche Meldung mit „Erneut versuchen“. |
| N-07 | Verfügbarkeit | Es gibt keine Garantie, da der Server zuhause läuft. Reminder, die während eines Ausfalls fällig wurden, werden nach dem Neustart nachgeschickt. |
| N-08 | Zeit | Alle Zeiten werden in UTC gespeichert und in Europe/Zurich angezeigt. Der Wechsel auf Sommer- und Winterzeit wird korrekt behandelt. |
| N-09 | Geld | Beträge werden als ganze Zahl in Rappen (bzw. der kleinsten Einheit der Währung) gespeichert, nie als Gleitkommazahl. |
| N-10 | Datenschutz | Finanzdaten bleiben auf dem eigenen Server. Push-Texte über Firebase enthalten nur das Nötige, also z. B. den Task-Titel, aber keine Kontodetails. |
| N-11 | Backup | Die Datenbank wird täglich automatisch auf ein zweites Speichermedium gesichert. Die Wiederherstellung ist einmal getestet. |

## Technische Architektur

&#91;embedded content: Systemarchitektur · App, Tunnel, Heimserver, Firebase, EZB\]

Die App erreicht den Server zuhause nur über den Cloudflare Tunnel. Push-Nachrichten schickt der Server über Firebase, das funktioniert ohne eingehende Verbindung zum Heimnetz.

| Bereich | Technologie |
| --- | --- |
| Repository | Gradle-Monorepo mit den Modulen `shared`, `composeApp` und `server`. Der Server nutzt die DTOs aus `shared`. |
| App-Oberfläche | Compose Multiplatform, zunächst nur Android |
| Gemeinsamer Code | Kotlin-Multiplatform-Modul `shared`: DTOs, Validierung, API-Client |
| Netzwerk in der App | Ktor Client mit kotlinx.serialization |
| Datum und Geld | kotlinx-datetime (`Instant`), Beträge als `Long` in Rappen |
| Backend | Spring Boot mit Kotlin, Spring Web, Spring Security (JWT), Spring Data JPA |
| Datenbank | PostgreSQL, Schema-Migrationen mit Flyway |
| Push | Firebase Cloud Messaging, im Server über das Firebase Admin SDK |
| Wechselkurse | Kostenlose API mit EZB-Referenzkursen, Kurse pro Tag im Server zwischengespeichert |
| Betrieb | Docker Compose mit Server, PostgreSQL und cloudflared |

## Nicht im Umfang von Version 1

- iOS-App und Web-Version (iOS ist durch Kotlin Multiplatform vorbereitet)
- Offline-Modus und lokale Datenspeicherung
- Ausgaben aufteilen und „wer schuldet wem“
- Mitgliedschaft in mehreren Haushalten
- Rolle „Kind“ mit eingeschränkten Rechten
- Login mit Google oder Apple, Versand von E-Mails
- Mehrere Reminder pro Task oder Reminder zu festem Zeitpunkt
- Bankanbindung oder Import von Kontoauszügen

## Annahmen und offene Punkte

Diese Punkte habe ich angenommen oder sie sind noch offen. Bitte bestätigen oder korrigieren, dann ist das Dokument vollständig.

**Annahmen**

- [ ] Die App ist auf Deutsch.
- [ ] Ein Einladungscode ist 7 Tage gültig.
- [ ] Mitglieder bearbeiten nur eigene Buchungen, Admins alle.
- [ ] Private Buchungen zählen nicht in Budgets und Haushaltsauswertungen.
- [ ] Tasks ohne Uhrzeit gelten um 08:00 als fällig. Daraus wird der Reminder berechnet.
- [ ] Die nächste Instanz eines wiederkehrenden Tasks entsteht nach Plan, auch wenn die vorherige noch nicht erledigt ist.
- [ ] Die Erinnerung für wiederkehrende Buchungen geht nur an die Person, die die Vorlage erstellt hat.

**Offene Fragen**

- [ ] Passwort vergessen: Setzt ein Admin ein temporäres Passwort, oder soll die App doch E-Mails versenden?
- [ ] Soll eine Push-Nachricht kommen, wenn einem ein Task neu zugewiesen wird?
- [ ] Sollen sich einzelne Benachrichtigungstypen in den Einstellungen abschalten lassen?
- [ ] Ist eine eigene Domain vorhanden? Ein dauerhafter Cloudflare Tunnel braucht eine Domain, die bei Cloudflare verwaltet wird.
- [ ] Auf welcher Hardware läuft der Server (Raspberry Pi, NAS, PC)? Davon hängen Docker-Images (ARM oder x86) und Leistung ab.
- [ ] Ist ein Export der Buchungen (z. B. als CSV) gewünscht?

## Meilensteine

Jeder Meilenstein ergibt eine lauffähige App. Die Reihenfolge folgt den Abhängigkeiten: ohne Login kein Haushalt, ohne Tasks keine Reminder.

1. **Grundgerüst und Login:** Gradle-Monorepo, Docker Compose mit Server und PostgreSQL, Registrierung, Login, Haushalt gründen (B-01 bis B-04, B-09).
2. **Haushalt und Mitglieder:** Einladungscode mit QR, Beitritt, Rollen, Mitglieder entfernen, Haushalt verlassen (B-05 bis B-08).
3. **Finanzen Basis:** Kategorien, Buchungen erfassen und bearbeiten, Liste mit Filter, Monatsübersicht (F-01 bis F-05, F-11).
4. **Tasks:** Erstellen, Zuweisen, Checklisten, Priorität, Wiederholung, Rotation, Ansichten (T-01 bis T-06, T-09 bis T-11).
5. **Push und Reminder:** Firebase einrichten, Scheduler im Server, Task-Reminder (T-07, T-08, N-07).
6. **Finanzen erweitert:** Budgets mit Warnungen, wiederkehrende Buchungen, Fremdwährungen, Diagramme (F-06 bis F-10, F-12).
7. **Betrieb:** Server zuhause einrichten, Cloudflare Tunnel, tägliche Backups, signierter Release-Build der App (N-01, N-11).
