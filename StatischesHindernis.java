/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Stellt ein sich nicht bewegendes Objekt dar, welches dem Spieler bei Kontakt(Spieler sei auf derselben Stelle wie StatHind.) Schaden zufügt
 * erbt von Objekt
 * @author Frausig
 */
public class StatischesHindernis extends Objekt {

    public StatischesHindernis(){
        identifier = 4;
        repr = 'G';
        farbe = Terminal.Color.RED;
    }
}
