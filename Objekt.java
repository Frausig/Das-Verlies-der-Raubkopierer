/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.terminal.Terminal;

/**
 * Stellt einen allgemeinen Bauplan für alle Spielobjekte dar, bis auf den SPieler selbst
 * @author Frausig
 */
public abstract class Objekt {
    public char repr;
    public Terminal.Color farbe;
    public int identifier;
}


