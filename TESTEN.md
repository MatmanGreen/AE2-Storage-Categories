# Testanleitung: Version 0.5.0

## Installation

1. Minecraft beenden. Eine Kopie der ATM10-Instanz mit Testwelt verwenden.
2. Die bisherige ae2-storage-categories-JAR aus dem mods-Ordner der Testkopie herausnehmen.
3. build/libs/ae2-storage-categories-0.5.0-prototype.jar dort hineinlegen. Nur eine Version gleichzeitig!
4. Minecraft starten. Die Originalinstanz wurde nicht automatisch veraendert.

Diese Version registriert erstmals einen echten Block mit gespeichertem Inventar. Vor einem Rueckwechsel auf 0.2 alle View Cells aus den Controllern nehmen. Die alte Version kennt diesen Block nicht.

## Erster Controller

1. ME Category Controller in JEI oder im Kreativinventar suchen (Funktionsbloecke).
   Mit Cheats alternativ: /give @s ae2categories:category_controller
   Vorlaeufiges Rezept: Eisen/Redstone/Eisen, Redstone/Truhe/Redstone, Eisen/Redstone/Eisen.
2. Controller direkt an ein ME-Kabel eines mit Strom versorgten Netzwerks stellen.
3. Eine AE2 View Cell in der AE2 Cell Workbench konfigurieren, z.B. mit Eichenbrettern und Glas.
4. Controller mit Rechtsklick oeffnen. Oben den Kategorienamen eingeben, z.B. Baumaterial.
5. Konfigurierte View Cell in einen der neun Slots legen. Es passen nur View Cells hinein, eine pro Slot.
6. Prioritaet zunaechst auf 0 lassen. Speichern anklicken (speichert Name/Prioritaet und schliesst das Fenster).
7. Normales oder Wireless Crafting Terminal im selben Netz oeffnen. Die passenden gespeicherten oder craftbaren Items stehen unter Baumaterial. Alle anderen unter Sonstiges.

View Cells speichern weiterhin nur Filter, keine Lageritems. Der Controller verbraucht 0,5 AE/t und benoetigt in diesem Prototyp keinen Kanal.
Die Oberflaeche meldet ME-Netz verbunden, sobald sein Knoten aktiv ist. Ein leerer oder unkonfigurierter Controller kategorisiert keine Items.
Aenderungen erscheinen normalerweise innerhalb von einer halben Sekunde im geoeffneten Terminal.

## Weitere Kategorien

- Pro Controller genau eine Kategorie. Mehrere View Cells werden mit AE2s eigener Filterlogik kombiniert.
- Hoehere Prioritaet steht weiter oben und gewinnt, wenn ein Item zu mehreren Kategorien passt.
- Bei gleicher Prioritaet entscheidet eine gespeicherte interne Kennung, dadurch bleibt die Reihenfolge stabil.
- Gleichnamige Controller bleiben getrennte Gruppen. Eindeutige Namen helfen bei der Bedienung.
- AE2-Fuzzy- und Inverter-Einstellungen der View Cells werden uebernommen. Ein invertierter Filter kann entsprechend viele Items erfassen.
- Ohne passende Items verschwindet eine Ueberschrift. Fluessigkeiten und andere Nicht-Item-Ressourcen bleiben unter Sonstiges.
- Die alten automatischen Gruppen Holz/Glas/Create/Baubloecke existieren in 0.3 nicht mehr; du legst die Kategorien jetzt selbst an.

## Terminal-Bedienung

- Linksklick auf eine Ueberschrift: ein-/ausklappen; + geschlossen, - offen.
- Suche findet auch Items in geschlossenen Kategorien. Nach Loeschen des Suchtextes kehrt der Klappzustand zurueck.
- F8: normale Ansicht/gruppierte Ansicht.
- Klappzustaende bleiben beim erneuten Oeffnen und nach Neustarts erhalten. Sie gelten pro Controller sowie Welt/Server und Spieler. Sonstiges wird pro Welt gemerkt.
- Bei Shift bleiben Positionen bei normalen Bestandsupdates stabil. Explizite Kategorie-/Filteraenderungen bauen die Gruppen neu auf.

## Bitte testen

1. Name eingeben (auch mit E im Namen), View Cells einfuegen/entnehmen, Shift-Klick und Speichern.
2. Zuordnung im kabelgebundenen und Wireless-Terminal pruefen.
3. Zweite View Cell hinzufuegen: Beide Filter muessen derselben Kategorie zugeordnet werden.
4. Zweiten Controller erstellen, Prioritaet aendern; absichtlich dasselbe Item in beiden filtern. Das Item darf nur einmal erscheinen.
5. Kabel zum Controller trennen: Seine Items wandern in andere passende Kategorien oder Sonstiges. Kabel wieder anschliessen.
6. Welt verlassen und neu laden: Name, Prioritaet, View Cells und Kategoriezuordnung bleiben erhalten.
7. Controller abbauen: View Cells muessen herausfallen; die Kategorie verschwindet. Name und Prioritaet werden beim erneuten Platzieren zurueckgesetzt.
8. Bisherige Tests wiederholen: Scrollen, Entnahme, Suche, Ein-/Ausklappen, Crafting und Autocrafting.

Bei Problemen: Terminaltyp, konkrete Aktion und Ergebnis nennen; bei Absturz logs/latest.log und den neuesten crash-reports-Bericht der Testinstanz.

## Teststatus

Der Build und die automatischen Layout-/Minecraft-Tests pruefen die technische Grundlage. Noch nicht bestaetigt ist die gesamte Bedienung in deinem ATM10-Modpack. Der erste Test sollte in einer Testwelt stattfinden.

## Zusaetzlicher Test 0.4
1. Eine Kategorie einklappen, Terminal schliessen und erneut oeffnen: bleibt geschlossen.
2. Zwischen normalem und Wireless-Terminal wechseln: gleicher Zustand.
3. Welt verlassen, Minecraft beenden/neustarten: Zustand bleibt erhalten.
4. Kategorie umbenennen: Klappzustand bleibt; danach aufklappen und erneut starten: bleibt offen.
5. Bestehenden Controller von allen Seiten ansehen: neues Modell ohne Neusetzen; Kabel und Inventar weiterhin pruefen.
6. Andere Welt: unabhaengige Klappzustaende. Eine verschobene/umbenannte lokale Welt verwendet einen neuen Einstellungskontext.
Modellvorschau: preview/controller-0.4.png. Die Vorschau ist gerendert, kein Ingame-Screenshot.


## Zusaetzlicher Test 0.5
1. Neue Texturen an platzierten Controllern und im Inventar ansehen; kein Neusetzen erforderlich.
2. Langen Kategorienamen vergeben und im Terminal darueberfahren: voller Name und Klickhinweis erscheinen.
3. Prioritaet leer, nur Minus oder 1000001 eingeben: rot und Speichern gesperrt. Mit einem gueltigen Wert wird Speichern wieder freigegeben.
4. Tab zwischen Name/Prioritaet wechseln, Enter speichert.
5. Bestehende Filter, Prioritaeten und gemerkte Klappzustaende bleiben erhalten.

