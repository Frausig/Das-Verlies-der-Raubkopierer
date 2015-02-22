/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;

/**
 * Mainklasse
 * @author Frausig
 */
public class Weihnachtsprojekt {
    
    public static void main(String[] args) {
        TerminalesMenu terminal = new TerminalesMenu();
        terminal.runMenu();
    }
}
