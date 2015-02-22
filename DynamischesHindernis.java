/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Stellt ein sich bewegendes Objekt dar, welches dem Spieler bei
 * Kontakt(Spieler sei auf derselben Stelle wie DynHind.) Schaden zufügt erbt
 * kann den spieler entdecken und verfolgen mit einem sichtfeld was genau alle vier himmelsrichtungen je 10 blöcke geht
 * von Objekt
 *
 * @author Frausig
 */
public class DynamischesHindernis extends Objekt {

    private char state;   //KI-Zustand: 's' = bewege auf zuletzt gesehene spielerkoords zu | 'h' = Kontakt mit Spieler, halte position | 'n' = kein Ziel, zufällige Bewegung
    private int[] xYLastSeen;   //letzte gesehene position des Spielers
    private int moveTimer;
    private int x;
    private int y;
    private int lastDirection; //stellt die zuletzt eingeschlagene RIchtung dar, 0=Norden | 1=Osten | 2=Süden | 3=Westen
    private final AudioPlayer busted;

    public DynamischesHindernis() {
        xYLastSeen = new int[2];
        identifier = 5;
        repr = '?';
        state = 'n';
        busted = new AudioPlayer();
        busted.init("busted");
        farbe = Terminal.Color.RED;
        moveTimer = (int) (Math.random() * 80 + 20);  //Gegner bewegt sich nach 20-100 Spieltaktzyklen das erste mal
    }

    public void pass() {         //wird bei jedem SPieltaktzyklus aufgerufen
        if (moveTimer > 0) {
            moveTimer--;  //dekrementiert den Timer für die nächste Bewegung
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public int getLastDirection() {
        return lastDirection;
    }

    public void setXY(int x_, int y_) {
        x = x_;
        y = y_;
    }

    public void spielerInSicht(int richtung, Objekt[][] spielfeld, int spielerX, int spielerY) { //prüft ob spieler tatsächlich im field of view ist und setzt richtung
        int[] cords = new int[2];
        cords[0] = x;
        cords[1] = y;
        for (int i = 0; i < 9; i++) {
            cords = getNextCords(richtung, cords[0], cords[1]);
            if (spielfeld[cords[0]][cords[1]] == null)
                ;
            else if (spielfeld[cords[0]][cords[1]] != null) {       //wand entdeckt
                return;
            } if (cords[0] == spielerX && cords[1] == spielerY) {      //spieler entdeckt
                if (state != 's') {
                    busted.start();
                    state = 's';
                }
                xYLastSeen[0] = spielerX;
                xYLastSeen[1] = spielerY;
                lastDirection = richtung;
                farbe = Terminal.Color.BLUE;
                repr = '!';
                return;
            }
        }
    }

    public void pruefeSpielerInSicht(int spielerX, int spielerY, Objekt[][] spielfeld) {    //prüft ob Kontakt mit spieler besteht, bzw ob spieler in reichweite ist oder nicht
        if (spielerX == x && spielerY == y) {   //falls der gegner kontakt mit dem spieler hat, returned er sofort -> keine bewegung, also keine berechnugen nötig
            state = 'h';
            lastDirection = -1;
            farbe = Terminal.Color.BLUE;
            repr = '!';
            xYLastSeen[0] = spielerX;
            xYLastSeen[1] = spielerY;
            return;
        }
        else if(state == 'h'){  //keine spielerbewegung vom eigenen Feld weg bemerkt -> setze auf "suchen"
            state = 'n';
            farbe = Terminal.Color.RED;
            repr = '?';
        }
        if (spielerY == y && Math.abs(x - spielerX) < 10) { //spieler ist auf derselben y-achse und in reichweite
            if (x - spielerX < 0) { //spieler ist rechts
                spielerInSicht(1, spielfeld, spielerX, spielerY);
            } else {   //spieler ist links
                spielerInSicht(3, spielfeld, spielerX, spielerY);
            }
        } else if (spielerX == x && Math.abs(y - spielerY) < 10) {  //spieler ist auf derselben x-Achse und in reichweite
            if (y - spielerY < 0) {   //spieler ist unten
                spielerInSicht(2, spielfeld, spielerX, spielerY);
            } else {       //spieler ist oben
                spielerInSicht(0, spielfeld, spielerX, spielerY);
            }
        }
        if(state == 's'){      //spieler aus den augen verloren
            if(x == xYLastSeen[0] && y == xYLastSeen[1]){
                lastDirection = -1;
                farbe = Terminal.Color.RED;
                repr = '?';
                state = 'n';
            }
        }
    }

    public int move(int spielerX, int spielerY, Objekt[][] spielfeld) {  //setzt den Timer zur nächsten Bewegung neu und berechnet eine Richtung
        System.out.println(state);
        pruefeSpielerInSicht(spielerX, spielerY, spielfeld);
        if (state == 'h') {
            moveTimer = (int) (Math.random() * 100 + 50 );
            return lastDirection;
        }
        if (state == 's') {
            moveTimer = 40;
            return lastDirection;
        }
        //elseTeil
        moveTimer = (int) (Math.random() * 30 + 30);
        lastDirection = (int) (Math.random() * 4);
        return lastDirection;
    }

    public int getMoveTimer() {
        return moveTimer;
    }
}
