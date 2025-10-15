# GhostPlayers - Minecraft Paper Plugin

Ein vollständiges Paper-Plugin für Minecraft 1.21.4, das Spieler serverseitig komplett vor anderen Spielern versteckt.

## Features

- **Vollständige Unsichtbarkeit**: Versteckte Spieler sind:
  - Nicht im Spiel sichtbar (kein Entity, keine Nametags)
  - Nicht in der Tab-Liste sichtbar
  - Nicht in Chat-Nachrichten sichtbar (Join/Quit, Death Messages, Chat)
  - Nicht im Tab-Complete sichtbar (weder Vanilla-Commands noch Plugin-Commands)
  - Nicht in Advancements oder Broadcasts sichtbar

- **Persistente Speicherung**: Alle Einstellungen bleiben nach Server-Restarts erhalten
- **Selektive Sichtbarkeit**: Du kannst einzelne Spieler vor bestimmten anderen Spielern verstecken
- **Einfache Verwaltung**: Intuitive Commands zur Verwaltung

## Installation

1. Kompiliere das Plugin mit Maven:
   ```bash
   mvn clean package
   ```

2. Die fertige JAR-Datei findest du in `target/GhostPlayers-1.0.0.jar`

3. Kopiere die JAR-Datei in den `plugins`-Ordner deines Paper-Servers

4. Starte den Server neu

## Commands

Alle Commands benötigen die Permission `ghostplayers.admin` (Standard: OP)

### Spieler verstecken

```
/ghost add <Spieler>
```
Versteckt einen Spieler vor allen anderen Spielern auf dem Server.

```
/ghost add <Spieler> <Betrachter>
```
Versteckt einen Spieler vor einem spezifischen Betrachter.

### Spieler sichtbar machen

```
/ghost remove <Spieler>
```
Macht einen Spieler wieder für alle sichtbar.

```
/ghost remove <Spieler> <Betrachter>
```
Macht einen Spieler für einen spezifischen Betrachter wieder sichtbar.

### Status überprüfen

```
/ghost list
```
Zeigt alle versteckten Spieler und ihre Sichtbarkeitseinstellungen.

```
/ghost visible <Spieler>
```
Zeigt an, vor welchen Spielern ein bestimmter Spieler versteckt ist.

## Aliases

Du kannst statt `/ghost` auch folgende Aliases verwenden:
- `/gp`
- `/hideplayer`

## Beispiele

### Beispiel 1: Spieler komplett verstecken
```
/ghost add Steve
```
Steve ist jetzt für alle anderen Spieler unsichtbar.

### Beispiel 2: Spieler nur vor bestimmten Personen verstecken
```
/ghost add Steve Alex
/ghost add Steve Bob
```
Steve ist jetzt nur für Alex und Bob unsichtbar, aber für alle anderen sichtbar.

### Beispiel 3: Spieler wieder sichtbar machen
```
/ghost remove Steve
```
Steve ist jetzt wieder für alle sichtbar.

## Technische Details

- **Version**: 1.0.0
- **Minecraft Version**: 1.21.4
- **Server Software**: Paper
- **Java Version**: 21

## Datenspeicherung

Die Konfiguration wird in `plugins/GhostPlayers/hidden-players.yml` gespeichert und bleibt nach Server-Restarts erhalten.

## Permissions

- `ghostplayers.admin` - Erlaubt die Verwaltung von versteckten Spielern (Standard: OP)

## Support

Bei Problemen oder Fragen kannst du ein Issue im Repository erstellen.
