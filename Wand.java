/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Klasse stellt ein sich nicht bewegendes Hindernis für den Spieler dar
 * erbt von Objekt
 * @author Frausig
 */
public class Wand extends Objekt {
    
    public Wand(){
        identifier = 0;
        repr = '\u2587';
        farbe = Terminal.Color.WHITE;
    }
}
