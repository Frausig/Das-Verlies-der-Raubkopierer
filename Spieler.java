/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Ein Objekt der KLasse Spieler stellt den Status wie Postition des Spielers bereit
 * @author Frausig
 */
public class Spieler{
    private Terminal.Color farbe = Terminal.Color.WHITE;
    private final char repr = '\u00AE';
    private int x;
    private int y;
    private int unverwundbar;       //Timer für Unverwundbarkeit
    private int leben;
    private String status;          //StatusLeiste wird vom Spieler verwaltet, aber nicht von ihm angezegt
    private int keys;           
    private Spiel spiel;      //Referenz auf SpielObjekt um sich selbst nezuzeichnen
    
    public Spieler(int x_, int y_, Spiel spiel_){
        x = x_;
        y = y_;
        keys = 0;
        unverwundbar = 0;
        leben = 3;
        spiel = spiel_;
        setStatus();
    }
    
    public void setFarbe(Terminal.Color farbe_){
        farbe = farbe_;
    }

    public void setLeben(int leben) {
        this.leben = leben;
        setStatus();
    }

    public void setKeys(int keys) {
        this.keys = keys;
        setStatus();
    }
    
    public void getHit(){
        if(unverwundbar == 0){    
            unverwundbar = 200;
            leben--;
            farbe = Terminal.Color.RED;
            spiel.zeichneSpNeu();
            spiel.audioHit.start();
        setStatus();
        }
        
    }
    
    public void setStatus(){      //aktualisiert die Statusleiste
        status = "\u2665"+"\u00D7"+ leben + " keys" + "\u00D7" + keys;
    }
    
    public String getStatus(){
        return status;
    }
    
    public int getSchluessel(){
        setStatus();
        return keys;
    }
    
    public void pass(){ // wird bei jedem Spieltaktzyklus aufegrufen
        if(unverwundbar > 0){
            unverwundbar--;
            if(unverwundbar == 0){
                farbe = Terminal.Color.WHITE;
                spiel.zeichneSpNeu();
            }
        }
    }
    
    public int getLeben(){
        return leben;
    }
    
    public int getUnverwundbar(){
        return unverwundbar;
    }
    
    public void collectKey(){
        keys++;
        setStatus();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Terminal.Color getFarbe() {
        return farbe;
    }

    public char getRepr() {
        return repr;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
