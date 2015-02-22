/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Eingang stellt den Anfang des Levels dar, d.h. der Spieler wird an der Postition des EIngangs starten
 * erbt von Objekt
 * @author Frausig
 */
public class Eingang extends Objekt{
    
    public Eingang(){
        identifier = 1;
        repr = 'e';
        farbe = Terminal.Color.WHITE;
    }
}
