# Technischer Stand 0.3.0

## Quellen

AE2 neoforge/v19.2.17, Commit 79ee2c704ad62941a426c26b1cb1f76ef5b2ee5a:
https://github.com/AppliedEnergistics/Applied-Energistics-2/tree/neoforge/v19.2.17

- Repo, RepoSlot, MEStorageScreen: Anzeigeprojektion und originale AE2-Interaktionen.
- ViewCellItem.createItemFilter: Cell-Workbench-Konfiguration, Fuzzy-/Inverter-Karten, mehrere View Cells.
- IManagedGridNode, IInWorldGridNodeHost, GridHelper, AECapabilities: eigener Netzwerkknoten und Kabelanschluss.
- IGrid.getActiveMachines: Controller des richtigen Netzwerks ermitteln.
- MEStorageMenu.getGridNode: serverseitiger Netzbezug fuer Terminals; AE2WTLib 19.5.1 WCTMenu ueberschreibt diese Methode mit dem Knoten seines Wireless-Hosts (Bytecode geprueft).

Eigene Implementierung, kein kopierter Storage-Organizer-Code.

## Controller und Server

Vanilla BlockEntity mit neun ItemStackHandler-Slots, nur View Cells, Stapellimit 1.
Name (64 Zeichen), Prioritaet (-1000000 bis 1000000), stabile UUID und komplette View-Cell-Komponenten werden in NBT gespeichert.
AE2-Netzwerkknoten an allen Seiten, 0,5 AE/t, kein Kanalbedarf. Registrierung ueber AE2s Node-Host-Capability.
Erstellung auf dem ersten Tick eines geladenen Chunks, Zerstoerung beim Entfernen/Entladen.
Beim Abbauen werden View Cells als Items ausgegeben. Der Blockdrop enthaelt noch keine kopierte Konfiguration.

Controller-Menue: echtes serverseitiges Inventar, neun Slots plus Spielerinventar, Shift-Klick, Statusanzeige.
Namens-/Prioritaets-Paket darf nur das aktuell geoeffnete, gueltige Controller-Menue des sendenden Spielers bearbeiten; keine freien Positionszugriffe.

Alle zehn Spielerticks: aus dem aktuell geoeffneten MEStorageMenu dessen Netz bestimmen, aktive Controller abfragen und nach Prioritaet/UUID sortieren.
Snapshots werden nur bei neuem Menue oder geaenderter Controller-Revision/-Zusammensetzung gesendet.
Ein Snapshot enthaelt Kennung, Namen und neun komplette View Cells je Controller. Server und Client verwenden normale NeoForge-Payloads.
Es werden keine Netzwerk-Lageritems kopiert oder veraendert.

## Client

Snapshots sind an die aktuelle Menueinstanz gebunden. Die Anzeigeprojektion ist je Repo getrennt.
Filter werden pro Snapshot einmal mit AE2s createItemFilter aufgebaut. Nullfilter (keine konfigurierten Cells) matcht keine Items.
Erste passende Kategorie gewinnt; UUID statt Anzeigename identifiziert eine Gruppe. Restkategorie Sonstiges ist separat.
Vier clientseitige Mixins behalten die funktionierende Raster-/Klicklogik von 0.2 bei und beziehen nun Kategorie-IDs und Namen aus den Controllern.
Suchtext/F8 wechseln auf die normale AE2-Liste. Einklappzustaende bleiben fuer die Lebensdauer des geoeffneten Terminals erhalten.

## Verifikation

- Fuenf JUnit-Layouttests: Gruppen, Fuellplaetze, Breiten, Ein-/Ausklappen, duplikatfreie Itemuebernahme.
- Zwei Minecraft-GameTests in separater Entwicklungswelt unter NeoForge 21.1.248 + AE2 19.2.17 + AE2WTLib 19.5.1 bestanden:
  - Gemeinsame Filter von zwei View Cells, Ausschluss nicht gelisteter Items, NBT-Roundtrip von Name/Prioritaet/UUID/Filterkomponenten, leere Filter.
  - Beitritt zu mit Strom versorgten ME-Netzen, Trennung zweier Netze, korrekte Maschinenzuordnung, Entfernen des Knotens beim Blockabbau.
- Controller-GUI, Paketempfang am echten Terminal, Wireless-Anzeige und Gesamtverhalten mit ATM10 muessen vom Nutzer getestet werden.

Die GameTest-Klassen unter dev und ihre leere Teststruktur sind aus der ausgelieferten Mod-JAR ausgeschlossen.

## Grenzen

- Internes AE2-GUI: Versionsbereich bewusst exakt 19.2.17.
- Nur geladene, aktive Controller zaehlen; keine eigene Chunk-Ladefunktion.
- Prototyp-Synchronisierung bis 256 Controller pro Netz (bei Ueberschreitung ausdrueckliche Meldung). Sehr grosse oder komponentenreiche Konfigurationen nicht lastgetestet.
- GUI-Texte zunaechst Deutsch, Platzhalter-Blockmodell mit Minecraft-Texturen, vorlaeufiges Rezept.
- Keine automatische Erhaltung von Name/Prioritaet beim Abbau; View Cells werden ausgegeben.
- Kein Speichern von Klappzustaenden ueber Terminalwechsel hinweg.
- Im Multiplayer ist die Mod jetzt auf Server und Clients erforderlich. Ziel bleibt zunaechst Einzelspieler.

## Build

Temurin JDK 21.0.13+9, Gradle 8.10.2 lokal unter .tools; Downloads SHA-256-geprueft.
ModDevGradle 2.0.148; NeoForge 21.1.248.
Unveraenderte AE2/GuideME/AE2WTLib-Abhaengigkeiten unter libs werden nicht in die Mod-JAR eingebettet.

./build.ps1 build
./build.ps1 runGameTestServer

## Erweiterung 0.4
CollapseStore: atomare JSON-Datei mit Zustandsmengen pro gehashtem Weltpfad/Server-Adresse und Spieler-UUID. Controller-UUID statt Name; Sonstiges pro Welt. Lesen einmal beim ersten Terminalzugriff, Schreiben bei Umschalten. Fehlerhafte Dateien werden nicht ueberschrieben; Fallback auf Sitzungszustand mit Logmeldung. Drei neue Tests fuer Neustart, Welttrennung und korrupte Daten.
47 Modellquader innerhalb der Blockgrenzen; AE2-Texturen per Ressourcenreferenz statt Kopien. Block- und Inventarmodell verwenden dasselbe Modell. Keine Aenderungen an Block-ID, Inventar oder Weltformat.


## Erweiterung 0.5
Eigene per Bildgenerator entworfene Texturen, atlasweise auf 32x32-Tiles aufbereitet; Original und Prompt unter art. Referenzanalyse aus der lokalen AE2-JAR. Modell auf einen Quader mit korrekten Face-UVs und Culling vereinfacht. Prioritaetsvalidierung ausgelagert in ControllerInput; zwei Tests fuer Grenzwerte und ungueltige Eingaben. Header-Tooltips in MEStorageScreen.renderTooltip, Hover in drawFG. Keine Aenderung am Welt-/Inventarformat. Weitere Befunde siehe VERBESSERUNGEN.md.

