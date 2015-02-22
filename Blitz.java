/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Blitz ist ein sich bewegendes Objekt auf dem Spielfeld, dass nur vom Spieler erzeugt werden kann
 * erbt von Objekt
 * @author Frausig
 */
public class Blitz extends Objekt {
    private final int richtung;     //Blitz bekommt bei der Erstellung eine Richtung mit welche final gesetzt wird
    private int bewegung;
    private int x;
    private int y;
    
    public Blitz(int richtung_, int x_, int y_){
        repr = '\u00A9';
        farbe = Terminal.Color.YELLOW;
        identifier = 6;
        richtung = richtung_;
        x = x_;
        y = y_;
        bewegung = 1;
    }
    
    public int getBewegung(){
        return bewegung;
    }
    
    public void pass(){   //wird bei jedem SpielTaktzyklus aufgerufen
        if(bewegung > 0){
            bewegung--; //Timer für die nächste Bewegung wird decrementiert
        }
    }

    public void setBewegung(int bewegung) {
        this.bewegung = bewegung;
    }
    
    public int getRichtung() {
        return richtung;
    }

    public int getX() {
        return x;
    }

    public void setXY(int[] xy){ //setzt die Position des Blitzes
        x = xy[0];
        y = xy[1];
    }

    public int getY() {
        return y;
    }
    
}
