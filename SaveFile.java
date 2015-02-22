/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

/**
 * Ein SafeFIleObjekt stellt intern einen Notizzettel dar welcher alle wichtigen Informationen eines Levels speichern kann, sowie sämtliche wichtige Spielerdaten
 * @author Frausig
 */
public class SaveFile {
    private Objekt[][] spielfeld;  //speichert das Spielfeld als zweidimensionales Array aus "Objekten" ab
    private int spielerX;
    private int spielerY;
    private int width;
    private int height;
    private int leben;
    private int schluessel;
    
    public SaveFile(){ //erster Konstruktor, hier ist nachträgliches Hinzufügen von Leveldaten erfoderlich
        
    }
    
    //zweiter Koonstruktor, bekommt alle Leveldaten bei Erzeugnug mit
    public SaveFile(Objekt[][] spielfeld_, int spielerX_, int spielerY_, int width_, int height_, int leben_, int schluessel_){
        spielfeld = spielfeld_;
        spielerX = spielerX_;
        spielerY = spielerY_;
        width = width_;
        height = height_;
        leben = leben_;
        schluessel = schluessel_;
    }

    public Objekt[][] getSpielfeld() {
        return spielfeld;
    }

    public void setSpielfeld(Objekt[][] spielfeld) {
        this.spielfeld = spielfeld;
    }

    public int getSpielerX() {
        return spielerX;
    }

    public void setSpielerX(int spielerX) {
        this.spielerX = spielerX;
    }

    public int getSpielerY() {
        return spielerY;
    }

    public void setSpielerY(int spielerY) {
        this.spielerY = spielerY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeben() {
        return leben;
    }

    public void setLeben(int leben) {
        this.leben = leben;
    }

    public int getSchluessel() {
        return schluessel;
    }

    public void setSchluessel(int schluessel) {
        this.schluessel = schluessel;
    }
    
    
}
