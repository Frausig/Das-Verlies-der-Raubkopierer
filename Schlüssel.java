/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Stellt ein sich nicht bewegendes Objekt dar, welches vom Spieler eingesammelt werden muss, um den Augang betreten zu können
 * erbt von Objekt
 * @author Frausig
 */
public class Schlüssel extends Objekt{

    public Schlüssel(){
        identifier = 3;
        repr = 'k';
        farbe = Terminal.Color.YELLOW;
    }
}