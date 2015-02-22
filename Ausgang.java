/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Klasse stellt ein sich nicht bewegendes Objekt dar welches vom Spieler auf dem SPielfeld erreicht werden muss, um das Spiel zu gewinnen
 * erbt von Objekt
 * @author Frausig
 */
public class Ausgang extends Objekt{

    public Ausgang(){
        identifier = 2;
        repr = 'E';
        farbe = Terminal.Color.GREEN;
    }
}
