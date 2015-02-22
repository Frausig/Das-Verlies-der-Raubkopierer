/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.screen.ScreenWriter;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import java.io.FileNotFoundException;

/**
 * Klasse stellt ein Hauptmenü bereit, sowohl verschiedene Handler für Rückgaben
 * von einem Objekt der Klasse Spiel
 *
 * @author Frausig
 */
public class TerminalesMenu {

    private final SwingTerminal terminal;
    private int currentX = 20;
    private int currentY = 20;
    private ConfigReader confr;
    private final Screen screen;
    private Spiel aktuellesSpiel;
    private final ScreenWriter writer;
    public AudioPlayer audioWin;
    public AudioPlayer audioGameOver;
    public AudioPlayer audioWrongFile;

    public TerminalesMenu() {
        terminal = TerminalFacade.createSwingTerminal();
        terminal.setCursorVisible(false);
        screen = new Screen(terminal);
        screen.startScreen();
        writer = new ScreenWriter(screen);
        audioWin = new AudioPlayer();
        audioWin.init("smw_course_clear");
        audioGameOver = new AudioPlayer();
        audioGameOver.init("smw_lost_a_life");
        audioWrongFile = new AudioPlayer();
        audioWrongFile.init("chord");
    }

    public void runMenu() {
        //Methode stellt Logik hinter dem Hauptmenü bereit
        drawMainMenu();
        terminal.setCursorVisible(true);
        terminal.moveCursor(currentX, currentX);
        TerminalSize tempSize = terminal.getTerminalSize();
        boolean fertig = false;
        while (fertig == false) {     //anfang while schleife, Menütakt
            if (screen.resizePending()) {
                tempSize = terminal.getTerminalSize();
                screen.completeRefresh();
                try {
                    drawMainMenu();
                    terminal.setCursorVisible(false);
                    terminal.moveCursor(currentX, currentX);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Key key = terminal.readInput();
            if (key == null) {

            } else if (key.getKind() == Key.Kind.Escape) {
                fertig = true;
            } else if (key.getKind() == Key.Kind.ArrowDown) {
                terminal.moveCursor(currentX, ++currentY);
            } else if (key.getKind() == Key.Kind.ArrowLeft) {
                terminal.moveCursor(--currentX, currentY);
            } else if (key.getKind() == Key.Kind.ArrowRight) {
                terminal.moveCursor(++currentX, currentY);
            } else if (key.getKind() == Key.Kind.ArrowUp) {
                terminal.moveCursor(currentX, --currentY);
            } else if (key.getKind() == Key.Kind.Enter) {
                int runRueckgabe = -1;
                //neues Spiel beginnen
                if (currentX > 9 && currentX < 30 && currentY == 10) {
                    try {
                        aktuellesSpiel = new Spiel(terminal, screen);
                        runRueckgabe = aktuellesSpiel.run();
                    } catch (FileNotFoundException e) {
                        drawNoNewGameScreen();
                        audioWrongFile.start();
                        pressAnyKey(0);
                    }
                } // spiel laden
                else if (currentX > 9 && currentX < 21 && currentY == 15) {
                    try {
                        SaveFile sf = loadSaveFile();
                        aktuellesSpiel = new Spiel(terminal, screen, sf);
                        runRueckgabe = aktuellesSpiel.run();
                    } catch (FileNotFoundException e) {
                        drawWrongNameScreen();
                        audioWrongFile.start();
                        pressAnyKey(1);
                    }
                } // spiel beenden
                else if (currentX > 9 && currentX < 23 && currentY == 20) {
                    fertig = true;
                } else if (currentX > 49 && currentX < 55 && currentY == 22) {
                    drawHilfeScreen();//5
                    pressAnyKey(5);
                } else if (currentX > 69 && currentX < 77 && currentY == 22) {
                    drawLegendeScreen();
                    pressAnyKey(4);
                }
                // behandlen der rückgabe von run
                if (runRueckgabe == 0) {
                    drawLoosingScreen(); // Spieler hat verloren
                    audioGameOver.start();
                    pressAnyKey(3);
                } else if (runRueckgabe == 2) {
                    saveHandler(aktuellesSpiel); //Spieler speichert sein Spiel ab
                } else if (runRueckgabe == 4) {
                    drawWinningScreen(); // Spieler hat gewonnen
                    audioWin.start();
                    pressAnyKey(2);
                }
                drawMainMenu();
                terminal.moveCursor(currentX, currentY);
            }
        }                           //ende While schleife, Menütakt
        screen.stopScreen();
    }

    public void drawLoosingScreen() {
        //wird aufgerufen wenn der Spieler alle seine Leben verloren hat
        screen.clear();
        screen.refresh();
        writer.fillScreen(' ');
        writer.drawString(30, 5, "Game Over");
        writer.drawString(30, 10, "press any key to continue", ScreenCharacterStyle.Blinking);
        screen.refresh();
        terminal.setCursorVisible(false);
    }

    public void drawWrongNameScreen() {
        //Wird aufgerufen wenn ein Spielstandname eingegeben wurde, zu welchem keine SPielstand existiert
        screen.clear();
        screen.refresh();
        writer.fillScreen(' ');
        writer.drawString(30, 5, "dieser Spielstand existiert nicht");
        writer.drawString(30, 6, "press any key to continue", ScreenCharacterStyle.Blinking);
        screen.refresh();
        terminal.setCursorVisible(false);
    }

    public void drawWinningScreen() {
        // winningscreen, wartet auf any key um zurückzugeben
        screen.clear();
        screen.refresh();
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.drawString(30, 5, "Glückwunsch, du bist erfolgreich aus dem Verlies entkommen");
        writer.drawString(30, 6, "press any key to continue", ScreenCharacterStyle.Blinking);
        screen.refresh();
        terminal.setCursorVisible(false);
    }

    public SaveFile loadSaveFile() throws FileNotFoundException {
        //Methode zeichnet einen Spielstand-Laden Screen
        //sie legt eine Safefile an und gibt diese zurück mit Daten aus einer Property-datei
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.drawString(15, 5, "Gebe den Spielstandnamen ein, den du laden willst(maximal 10 Zeichen):");
        screen.refresh();
        terminal.setCursorVisible(false);
        String fileName = gibEingabe(30, 10, false);
        SaveFile sf = SafeFileReader.readSaveFile(fileName); //liest aus einer Property-datei daten aus
        return sf;
    }

    public void drawMainMenu() {
        //Methode, die ein Hauptmenü zeichnet
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.drawString(43, 1, "Hauptmenü");
        writer.drawString(7, 5, "Wähle mithilfe der Pfeiltasten den gewünschten Button an und drücke 'Enter' zum bestätigen");
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.drawString(10, 10, "Neues Spiel Beginnen");
        writer.drawString(10, 15, "Spiel Laden");
        writer.drawString(10, 20, "Spiel Beenden");
        writer.drawString(70, 22, "Legende");
        writer.drawString(50, 22, "Hilfe");
        screen.refresh();
        terminal.setCursorVisible(true);
    }

    public void saveHandler(Spiel spiel) {
        //Methode legt ein Objekt der Klasse SafeFile an, welches als interner Notizblock für Leveldaten verwendet wird
        //Methode zeichnet auch den entsprechenden Screen
        SaveFile sf = new SaveFile();
        try {
            sf.setHeight(spiel.getConfr().getHeight());
            sf.setWidth(spiel.getConfr().getWidth());
            sf.setLeben(spiel.getSpieler().getLeben());
            sf.setSchluessel(spiel.getSpieler().getSchluessel());
            sf.setSpielerX(spiel.getSpieler().getX());
            sf.setSpielerY(spiel.getSpieler().getY());
            sf.setSpielfeld(spiel.getSpielfeld());
        } catch (Exception e) {
            sf.setHeight(spiel.oldSF.getHeight());
            sf.setWidth(spiel.oldSF.getWidth());
            sf.setLeben(spiel.getSpieler().getLeben());
            sf.setSchluessel(spiel.getSpieler().getSchluessel());
            sf.setSpielerX(spiel.getSpieler().getX());
            sf.setSpielerY(spiel.getSpieler().getY());
            sf.setSpielfeld(spiel.getSpielfeld());
        }
        terminal.clearScreen();
        screen.clear();
        screen.refresh();
        screen.putString(5, 14, "Gib einen Namen für deinen Speicherstand ein und bestätige ihn mit 'Enter' (maximal 10 zeichen)", Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(5, 15, "Verwende hierfür keine '\"', '/' oder '\\', andere Sonderzeichen sind erlaubt, sonst wird dein Spielstand nicht gespeichert", Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.refresh();
        terminal.setCursorVisible(false);
        String fileName = gibEingabe(30, 18, true);
        SaveFileWriter.createSaveFile(sf, fileName); // Speicherung der Daten aus dem SafeFile Objekt als properties Datei
    }

    public String gibEingabe(int x, int y, boolean aufrufer) {  //true == savehandler() | false == loadSaveFile()
        //Methode wird benutzt um Eingaben von Spielstandnamen, sowohl fürs laden als auch fürs speichern zu handlen
        String eingabe = "";
        int max = 10; // maximal 10 zeichen
        screen.putString(x, y, "          ", Terminal.Color.BLUE, Terminal.Color.BLUE);
        screen.refresh();
        terminal.setCursorVisible(false);
        while (true) {
            if (screen.resizePending()) {
                screen.clear();
                screen.completeRefresh();
                if (aufrufer == true) {
                    screen.putString(5, 14, "Gib einen Namen für deinen Speicherstand ein und bestätige ihn mit 'Enter' (maximal 10 zeichen)", Terminal.Color.WHITE, Terminal.Color.BLACK);
                    screen.putString(5, 15, "Verwende hierfür keine '\"', '/' oder '\\', andere Sonderzeichen sind erlaubt, sonst wird dein Spielstand nicht gespeichert", Terminal.Color.WHITE, Terminal.Color.BLACK);

                } else {
                    writer.setBackgroundColor(Terminal.Color.BLUE);
                    writer.setForegroundColor(Terminal.Color.WHITE);
                    writer.fillScreen(' ');
                    writer.drawString(15, 5, "Gebe den Spielstandnamen ein, den du laden willst(maximal 10 Zeichen):");
                }
                screen.putString(x, y, eingabe, Terminal.Color.WHITE, Terminal.Color.BLUE);
                screen.refresh();
                terminal.setCursorVisible(false);
            }
            Key key = terminal.readInput();
            if (key == null)
                ; else if (key.getKind() == Key.Kind.NormalKey && eingabe.length() < max) {
                eingabe = eingabe + key.getCharacter();
                screen.putString(x, y, eingabe, Terminal.Color.WHITE, Terminal.Color.BLUE);
                screen.refresh();
                terminal.setCursorVisible(false);
            } else if (key.getKind() == Key.Kind.Enter) {
                return eingabe;
            } else if (key.getKind() == Key.Kind.Backspace && eingabe.length() >= 1) {
                eingabe = eingabe.substring(0, eingabe.length() - 1);
                screen.putString(x, y, "          ", Terminal.Color.BLUE, Terminal.Color.BLUE);
                screen.putString(x, y, eingabe, Terminal.Color.WHITE, Terminal.Color.BLUE);
                screen.refresh();
                terminal.setCursorVisible(false);
            }
        }
    }

    private void drawNoNewGameScreen() {
        // NoNewGameScreen, wird gezeichnet wenn keine Spieldatei gefunden wurde nachdem "Neues Spiel" angewählt wurde
        screen.clear();
        screen.refresh();
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.drawString(5, 5, "Es wurde kein Spiel gefunden, generiere deshalb erst eines und verschiebe es in das selbe");
        writer.drawString(5, 10, "...oder die Leveldatei ist beschädigt, generiere auch hierfür einen neuen Level");
        writer.drawString(5, 6, "Verzeichnis, wie das Spiel.");
        writer.drawString(30, 15, "press any key to continue", ScreenCharacterStyle.Blinking);
        screen.refresh();
        terminal.setCursorVisible(false);
    }

    private void drawLegendeScreen() {
        screen.clear();
        screen.refresh();
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.drawString(20, 4, "_Legende_");
        screen.putString(5, 10, "?", Terminal.Color.RED, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 10, " = suchendes dynamisches Hindernis('Raubkopierer')", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 11, "!", Terminal.Color.BLUE, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        screen.putString(6, 11, " = jagendes dynamisches Hindernis('Raubkopierer')", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 12, "G", Terminal.Color.RED, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 12, " = statisches Hindernis('Stacheln')", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 13, "k", Terminal.Color.YELLOW, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 13, " = Schlüssel", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 14, "e", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 14, " = Eingang", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 15, "E", Terminal.Color.GREEN, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 15, " = Ausgang", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 16, "\u00A9", Terminal.Color.YELLOW, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 16, " = Copyright-Geschoss", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(5, 17, "\u00AE", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.putString(6, 17, " = Spieler('Anwalt der GEMA')", Terminal.Color.WHITE, Terminal.Color.BLUE, ScreenCharacterStyle.Bold);
        screen.refresh();
        terminal.setCursorVisible(false);
    }

    private void pressAnyKey(int aufrufer) { //allgemeine Hold-Methode die mit einem Parameter mehrere Menüs rdarstellen kann bis eine beliebige Taste gedrückt wird
        try {
            boolean fertig = false;
            while (fertig == false) {
                Key key = terminal.readInput();
                if (key != null) {
                    fertig = true;
                }
                if (screen.resizePending()) {
                    screen.completeRefresh();
                    switch (aufrufer) {
                        case 0:
                            drawNoNewGameScreen();
                            break;
                        case 1:
                            drawWrongNameScreen();
                            break;
                        case 2:
                            drawWinningScreen();
                            break;
                        case 3:
                            drawLoosingScreen();
                            break;
                        case 4:
                            drawLegendeScreen();
                            break;
                        case 5:
                            drawHilfeScreen();
                            break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawHilfeScreen() {        //malt den Hilfescreen
        screen.clear();
        screen.refresh();
        writer.setBackgroundColor(Terminal.Color.BLUE);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.fillScreen(' ');
        writer.setBackgroundColor(Terminal.Color.BLACK);
        writer.setForegroundColor(Terminal.Color.WHITE);
        writer.drawString(30, 3, "_Hilfe_", ScreenCharacterStyle.Bold);
        writer.drawString(15, 7, "Steuerung:", ScreenCharacterStyle.Underline);
        writer.drawString(5, 8, "- Mit den Pfeiltasten wird der Spieler bewegt");
        writer.drawString(5, 9, "- Mit den WASD-Tasten wird ein CopytightProjektil verschossen");
        writer.drawString(5, 10, "- Mit der Escape-Taste kommt man jederzeit in ein Pausenmenü");
        writer.drawString(15, 13, "Regeln:", ScreenCharacterStyle.Underline);
        writer.drawString(5, 14, "- Ziel des Spiels ist es mit mindestens einem Schlüssel den Ausgang zu erreichen");
        writer.drawString(5, 15, "- Schlüssel kann man einsammeln, wenn man über sie läuft");
        writer.drawString(5, 16, "- es sollte vermieden werden über Gegner('Raubkopierer') oder StatHind. hinwegzulaufen, da hier ein Leben verloren wird");
        writer.drawString(5, 17, "- obige kann man mithilfe eines CopyrightProjektils beseitigen, hierfür wird ein Schlüssel verbraucht");
        writer.drawString(5, 18, "- der Spieler beginnt mit drei Leben und null Schlüsseln");
        writer.drawString(5, 19, "- Gegner können den Spieler bei einem direkten Sichtfeld und einer Entfernung von weniger als 10 Blöcken entdecken");
        writer.drawString(5, 20, "- Gegner die einen Spieler entdeckt haben, färben sich blau und bewegen sich unweigerlich auf die zuletzt gesehene Postion des Spielers zu");
        writer.drawString(5, 21, "- solltest du genug von einem Level haben, erstelle mit der generate.jar und der zugehörigen parameters.txt einen neuen, wie in der readme beschrieben");
        screen.refresh();
        terminal.setCursorVisible(false);
    }
}
