/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenCharacterStyle;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SpielObjekt stellt die Logik und Darstellung des eigentlichen Spiels bereit,
 * inklusive Gegnerbewegung, Kollisionen und halbwegs intelligenter Darstellung
 *
 * @author Frausig
 */
public class Spiel {

    private Terminal terminal; //globales Terminal
    private Screen screen;  //globaler Screen
    private char[][] screenBuffer;  //stellt einen ähnlichen Buffer für die Darstellung im Terminal bereit, wie er beim Screen schon inbegriffen ist
    private Objekt[][] spielfeld;   //Stellt das ganze Spielfeld als 2-D Array von Objekten dar, also alles bis auf den SPieler...der kriegt ne extra wurscht
    private ConfigReader confr;
    private Spieler spieler;        //SPieler wird einzeln global vom Spiel verwaltet
    private LinkedList<DynamischesHindernis> enemyList = new LinkedList(); //Verwaltung aller DynamischenHindernisse auf dem Spielfeld
    private TerminalSize screenSize;    //globale Referenz auf ein TerminalSizeObjekt
    private long taktCount;             //ein lediglich zu Testzwecken gedachter SpielTaktCounter
    private LinkedList<Blitz> blitzeList = new LinkedList();    //Verwaltung aller BlitzObjekten auf dem SPielfeld
    public AudioPlayer mainTheme;
    public AudioPlayer audioCoin;
    public AudioPlayer audioFireball;
    public AudioPlayer audioHit;
    public AudioPlayer audioPause;
    public AudioPlayer audioKill;
    public SaveFile oldSF;

    public Spiel(Terminal terminal_, Screen screen_) throws FileNotFoundException {  //erster Kontruktor, welche lediglich ein Terminal und einen Screen bekommt
        soundInit();
        terminal = terminal_;
        terminal.setCursorVisible(false);
        screen = screen_;
        try{
            confr = new ConfigReader();             //erstellt einen ConfigReader um daraus den Level zu bekommen  
        }catch(Exception e){
            mainTheme.stop();
            throw new FileNotFoundException(e.getMessage());
        }
        spielfeld = confr.getSpielfeld();
        terminal.clearScreen();
        screenSize = terminal.getTerminalSize();
        screenBuffer = new char[screenSize.getColumns()][screenSize.getRows()]; //screenBuffer wird entprechend der TerminalSize initalisiert  
        //screenBuffer initialisiern -> alles mit ' ' ausfüllen
        screenBufferInit();
        taktCount = 0;
        for (int i = 0; i < confr.getWidth(); i++) {
            for (int j = 0; j < confr.getHeight(); j++) {
                if (spielfeld[i][j] != null) {
                    if (spielfeld[i][j].identifier == 5) {                        //liste aller dynamischen hindernisse/ für allgemeine ansteuerung
                        enemyList.add((DynamischesHindernis) spielfeld[i][j]);
                        enemyList.getLast().setXY(i, j);                        //nachträgliches setzen der koordinaten, um einen listendurchgang zu ersparen!nicht optimieren!
                    } else if (spielfeld[i][j].identifier == 1 && spieler == null) {
                        spieler = new Spieler(i, j, this);
                    }
                }
            }
        }
        terminal.clearScreen();
        screen.clear();
        screen.refresh();
    }

    public Spiel(Terminal terminal_, Screen screen_, SaveFile sf) { //zweiter Konstruktor, wird verwendet um ein SPiel aus einer SaveFile zu erstellen
        soundInit();
        oldSF = sf;
        terminal = terminal_;
        screen = screen_;
        spielfeld = sf.getSpielfeld();
        spieler = new Spieler(sf.getSpielerX(), sf.getSpielerY(), this);
        spieler.setKeys(sf.getSchluessel());
        spieler.setLeben(sf.getLeben());
        terminal.clearScreen();
        screenSize = terminal.getTerminalSize();
        screenBuffer = new char[screenSize.getColumns()][screenSize.getRows()];//screenBuffer wird entprechend der TerminalSize initalisiert  
        //screenBuffer initialisiern -> alles mit ' ' ausfüllen
        screenBufferInit();
        taktCount = 0;
        for (int i = 0; i < sf.getWidth(); i++) {
            for (int j = 0; j < sf.getHeight(); j++) {
                if (spielfeld[i][j] != null) {
                    if (spielfeld[i][j].identifier == 5) {                        //liste aller dynamischen hindernisse/ für allgemeine ansteuerung
                        enemyList.add((DynamischesHindernis) spielfeld[i][j]);
                        enemyList.getLast().setXY(i, j);                        //nachträgliches setzen der koordinaten, um einen listendurchgang zu ersparen!nicht optimieren!
                    }
                }
            }
        }
        terminal.clearScreen();
        screen.clear();
        screen.refresh();
    }

    public void soundInit() {//windows sound einbaun
        mainTheme = new AudioPlayer();
        audioCoin = new AudioPlayer();
        audioFireball = new AudioPlayer();
        audioHit = new AudioPlayer();
        audioPause = new AudioPlayer();
        audioKill = new AudioPlayer();
        mainTheme.init("mainTheme");
        audioFireball.init("smw_fireball");
        audioHit.init("getHit");
        audioPause.init("smw_pause");
        audioKill.init("killEnemy");
        audioCoin.init("smw_coin");
        mainTheme.loop();
    }

    public void screenBufferInit() { //screenBuffer initialisiern -> alles mit ' ' ausfüllen
        for (int i = 0; i < screenSize.getColumns(); i++) {
            for (int j = 1; j < screenSize.getRows(); j++) {
                screenBuffer[i][j] = ' ';
            }
        }
    }

    public int pausenMenu() { // gibt int entsprechend der aktion zurück | 1 = fortsetzen | 2 = speichern und beenden | 3 = beenden ohne zu speichern | 
        //Folgende Zeilen stellen Logik für das Pausenmenü bereit
        drawPausenMenu();
        while (true) {
            if (screen.resizePending() || terminal.getTerminalSize().getColumns() != screenSize.getColumns() || terminal.getTerminalSize().getRows() != screenSize.getRows()) {
                screenSize = terminal.getTerminalSize();
                screenBuffer = new char[screenSize.getColumns()][screenSize.getRows()];
                terminal.clearScreen();
                screen.refresh();
                drawPausenMenu();
                screenBufferInit();
            }
            Key key = terminal.readInput();
            if (key == null)
                ; else if (key.getKind() == Key.Kind.Enter) { //Spiel fortsetzen
                terminal.clearScreen();
                screen.clear();
                screen.refresh();
                zeichneSpNeu();
                screenBufferInit();
                return 1;
            } else if (key.getKind() == Key.Kind.ArrowDown) {   //Spiel speichern
                screen.clear();
                return 2;
            } else if (key.getKind() == Key.Kind.Escape) {      //Spiel beenden
                screen.clear();
                return 3;
            }
        }
    }

    public void drawPausenMenu() {  //malt das Pausenmenü
        terminal.clearScreen();
        screen.clear();
        screen.refresh();
        screen.putString(0, 0, "Drück die Entertaste um das spiel fortzusetzen", Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(0, 2, "Drück die Pfeil-Untentaste um das spiel zu speichern und zu beenden", Terminal.Color.WHITE, Terminal.Color.BLACK);
        screen.putString(0, 4, "Drück die Esctaste um das spiel ohne zu speichern zu beenden", Terminal.Color.WHITE, Terminal.Color.BLACK);
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

    public ConfigReader getConfr() {
        return confr;
    }

    public void blitzPass() {   //Methode wird jeden Spieltaktzyklus aufgerufen; bewegt alle Blitz-Objekte entsprechend ihren Timern
        ArrayList<Blitz> tempBlitz = new ArrayList();
        for (Blitz durchgang : blitzeList) {
            durchgang.pass();
            if (durchgang.getBewegung() == 0) {
                if (getCollisionBlitz(durchgang.getRichtung(), durchgang.getX(), durchgang.getY())) {
                    int[] nextCords = getNextCords(durchgang.getRichtung(), durchgang.getX(), durchgang.getY());
                    spielfeld[durchgang.getX()][durchgang.getY()] = null;
                    durchgang.setXY(nextCords);
                    spielfeld[nextCords[0]][nextCords[1]] = durchgang;
                    durchgang.setBewegung(3);
                } else {    //merkt sich Objekt zum löschen, bei Kollision mit Wand oder Schlüsseln
                    spielfeld[durchgang.getX()][durchgang.getY()] = null;
                    tempBlitz.add(durchgang);
                }
            }
        }
        for (Blitz blitz : tempBlitz) {
            blitzeList.remove(blitz);
        }
    }

    //Folgende MEthode wird beim SPielstart aufgerufen und stellt den genauen Ablauf der SPiellogik dar
    public int run() {// Rueckgabe: 0 = spieler hat verloren (alle leben verloren) | 2 = speichern und zum hauptmenue | 3 = nicht speichern und zum hauptmenue | 4 gewonnen und zum hauptmenue
        zeichneSpNeu();
        int pausenMenu = 1;
        while (pausenMenu == 1) {   //Anfang Soeiltaktzyklus-schleife
            if (screenSize.getColumns() != terminal.getTerminalSize().getColumns() || screenSize.getRows() != terminal.getTerminalSize().getRows()) {
                screen.refresh();
                screenSize = terminal.getTerminalSize();
                screenBuffer = new char[screenSize.getColumns()][screenSize.getRows()];
                screenBufferInit();
                terminal.clearScreen();
                screen.clear();
                screen.refresh();
                terminal.clearScreen();
                zeichneSpNeu();
                System.out.println("terminal ist up to date");
            }
            //pass methoden
            spieler.pass(); // wird  bei jedem takzyklus aufgerufen
            blitzPass();
            try {                                                               //taktbegrenzung
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Spiel.class.getName()).log(Level.SEVERE, null, ex);
            }

            //taktzyklus (spielerbewegung, gegnerbewegung, kollisionerkennung
            Key key = terminal.readInput();
            int SpX = spieler.getX();
            int SpY = spieler.getY();
            if (key == null)
                ; else if (key.getKind() == Key.Kind.ArrowDown) {     //Spielerbewegung nach unten
                try {
                    if (getCollisions(2, SpX, SpY, false)) {
                        spieler.setY(SpY + 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            } else if (key.getKind() == Key.Kind.ArrowLeft) {   //Spielerbewegung nach links
                try {
                    if (getCollisions(3, SpX, SpY, false)) {
                        spieler.setX(SpX - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            } else if (key.getKind() == Key.Kind.ArrowUp) {     //Spielerbewegung nach oben
                try {
                    if (getCollisions(0, SpX, SpY, false)) {
                        spieler.setY(SpY - 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            } else if (key.getKind() == Key.Kind.ArrowRight) {      //Spielerbewegung nach rechts
                try {
                    if (getCollisions(1, SpX, SpY, false)) {
                        spieler.setX(SpX + 1);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            } else if (key.getKind() == Key.Kind.Escape) {          //Pausenmenü aufrufen
                mainTheme.stop();               //spielmusik anhalten
                audioPause.start();
                pausenMenu = pausenMenu();  //Spielereingabe im Pausenmenüs abspeichern
                if(pausenMenu == 1){
                    audioPause.start();
                    mainTheme.loop();
                }
            } else if (key.getKind() == Key.Kind.NormalKey && key.getCharacter() == 'w') {  //Spieler schiesst nach oben
                if (hasKey()) {
                    erzeugeBlitz(0);

                }
            } else if (key.getKind() == Key.Kind.NormalKey && key.getCharacter() == 'a') {  //Spieler schiesst nach links
                if (hasKey()) {
                    erzeugeBlitz(3);

                }
            } else if (key.getKind() == Key.Kind.NormalKey && key.getCharacter() == 's') {  //Spieler schiesst nach unten
                if (hasKey()) {
                    erzeugeBlitz(2);
                }
            } else if (key.getKind() == Key.Kind.NormalKey && key.getCharacter() == 'd') {  //Spieler schiesst nach rechts
                if (hasKey()) {
                    erzeugeBlitz(1);
                }
            }
            //spielerkoords nicht mehr aktuell
            //gegnerbewegung ||
            //               \/
            ArrayList<Integer> loeschen = new ArrayList();
            for (DynamischesHindernis enemy : enemyList) {
                enemy.pass();
                if (enemy.equals(spielfeld[enemy.getX()][enemy.getY()]) == false) {
                    loeschen.add(enemyList.indexOf(enemy));
                } else if (enemy.getMoveTimer() == 0) {
                    int temp = enemy.move(spieler.getX(), spieler.getY(), spielfeld);
                    
                    if (temp != -1 && getCollisions(temp, enemy.getX(), enemy.getY(), true)) {
                        int[] nextCords = getNextCords(enemy.getLastDirection(), enemy.getX(), enemy.getY());
                        spielfeld[nextCords[0]][nextCords[1]] = spielfeld[enemy.getX()][enemy.getY()];
                        spielfeld[enemy.getX()][enemy.getY()] = null;
                        enemy.setXY(nextCords[0], nextCords[1]);
                    }
                }
            }
            //getroffene gegner loeschen
            for (int i = 0; i < loeschen.size(); i++) {
                DynamischesHindernis tempGegner = enemyList.get(loeschen.get(i));
                enemyList.remove(tempGegner);
            }
            //screenBuffer neu berechnen und Terminal entpsrechend neu zeichnen
            terminal.applySGR(Terminal.SGR.ENTER_BOLD);
            for (int i = 0; i < screenSize.getColumns(); i++) {
                for (int j = 1; j < screenSize.getRows(); j++) {
                    try {
                        int x = i + spieler.getX() - (screenSize.getColumns()) / 2; //Berechnen der Spielfeldkoordinate vom Spieler als Mittelpunkt des Terminals ausgehend,
                        int y = j + spieler.getY() - (screenSize.getRows()) / 2;    //während i und j über das Terminal iterieren 
                        if (spielfeld[x][y] == null && screenBuffer[i][j] != ' ' && (i != screenSize.getColumns() / 2 || j != screenSize.getRows() / 2)) {
                            terminal.moveCursor(i, j);
                            terminal.applyBackgroundColor(Terminal.Color.BLACK);
                            terminal.putCharacter(' ');
                            screenBuffer[i][j] = ' ';
                        } else if (spielfeld[x][y] == null) {
                            terminal.applyBackgroundColor(Terminal.Color.BLACK);
                            screenBuffer[i][j] = ' ';
                        } else if (spielfeld[x][y].repr != screenBuffer[i][j] && (i != screenSize.getColumns() / 2 || j != screenSize.getRows() / 2)) {
                            screenBuffer[i][j] = spielfeld[x][y].repr;
                            terminal.moveCursor(i, j);
                            terminal.applyForegroundColor(spielfeld[x][y].farbe);
                            if (spielfeld[x][y].identifier == 0) {
                                terminal.applyBackgroundColor(Terminal.Color.WHITE);
                            } else {
                                terminal.applyBackgroundColor(Terminal.Color.BLACK);
                            }
                            terminal.putCharacter(spielfeld[x][y].repr);
                        }
                    } catch (IndexOutOfBoundsException e) { //Alles  ausserhalb des Spielfeldes wird mit ' ' übermalt
                        if (screenBuffer[i][j] != ' ') {
                            terminal.applyBackgroundColor(Terminal.Color.BLACK);
                            screenBuffer[i][j] = ' ';
                            terminal.moveCursor(i, j);
                            terminal.putCharacter(' ');
                        }
                    }
                }
            }
            terminal.applySGR(Terminal.SGR.EXIT_BOLD);
            //Spieler stats neu aufs Terminal Zeichnen
            screen.putString(0, 0, spieler.getStatus() + "                               "
                    + "                                  ", Terminal.Color.WHITE, Terminal.Color.BLACK); //überschreiben der ersten screenzeile mit 'leer'
            screen.refresh();
            terminal.setCursorVisible(false);
            if (spielfeld[spieler.getX()][spieler.getY()] != null) { // nullpointer verhindern
                if (spielfeld[spieler.getX()][spieler.getY()].identifier == 4 || spielfeld[spieler.getX()][spieler.getY()].identifier == 5) {
                    //gegner ist unter spieler
                    spieler.getHit();
                }
            }
            if (spieler.getLeben() == 0) {
                pausenMenu = 0; //spielende durch game over
            }
            if (spielfeld[spieler.getX()][spieler.getY()] != null && spielfeld[spieler.getX()][spieler.getY()].identifier == 2) {
                return 4;
            }
        }       //Ende Spieltaktzyklus-schleife
        mainTheme.stop();
        return pausenMenu;
    }   //Ende von "run"

    public void erzeugeBlitz(int richtung) {
        audioFireball.start();
        int[] nextCords = getNextCords(richtung, spieler.getX(), spieler.getY());
        if (spielfeld[nextCords[0]][nextCords[1]] == null) {
            spielfeld[nextCords[0]][nextCords[1]] = new Blitz(richtung, nextCords[0], nextCords[1]);
            blitzeList.add((Blitz) spielfeld[nextCords[0]][nextCords[1]]);
        } else if (spielfeld[nextCords[0]][nextCords[1]].identifier == 4 || spielfeld[nextCords[0]][nextCords[1]].identifier == 5) {
            audioKill.start();
            spielfeld[nextCords[0]][nextCords[1]] = new Blitz(richtung, nextCords[0], nextCords[1]);
            blitzeList.add((Blitz) spielfeld[nextCords[0]][nextCords[1]]);
        }
    }

    public boolean hasKey() { //gibt zurück ob ein schlüssel vorhanden ist und zieht einen ab
        if (spieler.getSchluessel() > 0) {
            spieler.setKeys(spieler.getSchluessel() - 1);
            spieler.setStatus();
            return true;
        }
        return false;
    }

    public void zeichneSpNeu() { //Zeichnet den SPieler immer in das Zentrum des Terminals
        terminal.moveCursor(screenSize.getColumns() / 2, screenSize.getRows() / 2);
        terminal.applyForegroundColor(spieler.getFarbe());
        terminal.applyBackgroundColor(Terminal.Color.BLACK);
        terminal.applySGR(Terminal.SGR.ENTER_BOLD);
        terminal.putCharacter(spieler.getRepr());
        terminal.applySGR(Terminal.SGR.EXIT_BOLD);
    }

    public int[] getNextCords(int richtung, int x, int y) {     //allgemeine Methode um Koordinaten anhand einer Position und einer Richtung zu berechnen 
        int[] ausgabe = new int[2];
        switch (richtung) {
            case 0:
                ausgabe[0] = x;
                ausgabe[1] = y - 1;
                break;
            case 1:
                ausgabe[0] = x + 1;
                ausgabe[1] = y;
                break;
            case 2:
                ausgabe[0] = x;
                ausgabe[1] = y + 1;
                break;
            case 3:
                ausgabe[0] = x - 1;
                ausgabe[1] = y;
                break;
        }
        return ausgabe;
    }

    public boolean getCollisionBlitz(int richtung, int x, int y) {  //Kollisionsberechnung der BlitzObjekte
        int[] nextCords = getNextCords(richtung, x, y);
        Objekt collision = spielfeld[nextCords[0]][nextCords[1]];
        if (collision == null) {
            return true;
        }
        switch (collision.identifier) {
            case 0:             //Kollision mit Wand
                return false;
            case 1:             //Kollision mit Eingang
                return false;
            case 2:             //Kollision mit Ausgang
                return false;
            case 3:             //Kollision mit Schlüssel
                return false;
            case 4:             //Kollision mit StatischesHindernis
                audioKill.start();
                return true;
            case 5:             //Kollision mit DynamischesHindernis
                audioKill.start();
                return true;
            case 6:             //Kollision mit Blitz
                return false;
            default:
                return false;
        }
    }

    //Kollisionsberechnung des Spielers mit allen Objekten oder DynamischesHindernis mit allen Objekten
    public boolean getCollisions(int richtung, int x, int y, boolean enemyCall) throws IndexOutOfBoundsException { // boolean ist für die unterscheidung, ob methode von einem dynamischen hindernis aufegrufen wurde
        int[] nextCords = getNextCords(richtung, x, y);  //gibt koordinaten von einem standpunkt mit einer richtung an
        Objekt collision = spielfeld[nextCords[0]][nextCords[1]]; // speichert das objekt element an den oben errechneten koords ab
        //spielerCall
        if (collision == null && enemyCall == false) {
            return true;
        }
        //enemyCall
        if (enemyCall && collision == null) {
            return true;
        }
        //spieler & enemyCall
        switch (collision.identifier) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                //enemyCall
                if (enemyCall) {
                    return false;
                }
                //spielerCall
                if (spieler.getSchluessel() > 0) {
                    mainTheme.stop();
                    return true;//erkennung ob er alle schlüssel hat, dann spiel vorbei und gewinn screen
                }
                return false;
            case 3:
                //enemyCall
                if (enemyCall) {
                    return false;
                }
                //spielerCall
                spielfeld[nextCords[0]][nextCords[1]] = null;
                spieler.collectKey();
                audioCoin.start();
                return true; //schlüssel löschen bzw auf spieler objekt übertragen
            case 4:
                //enemyCall
                if (enemyCall) {
                    return false;
                }
                //spielerCall
                spieler.getHit();
                return true; // leben abgezogen, unverwundbarkeit für 5 sekunden an
            case 5:
                //enemyCall
                if (enemyCall) {
                    return false;
                }
                //spielerCall
                spieler.getHit();
                return true; // leben abgezogen bekommen
            case 6:
                return false;
        }
        //enemyCall
        if (enemyCall == true && nextCords[0] == spieler.getX() && nextCords[1] == spieler.getY()) {
            spieler.getHit();
            audioHit.start();
            return true;
        }
        return false;
    }

    public Spieler getSpieler() {
        return spieler;
    }

    public Objekt[][] getSpielfeld() {
        return spielfeld;
    }
}
