/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WIrd dazu benutzt um aus als property datei gespeicherten Spielständen ein Objekt der KLasse SafeFile zu erstellen
 * @author Frausig
 */
public class SafeFileReader {
    
    public static SaveFile readSaveFile(String fileName) throws FileNotFoundException{
        Properties props = new Properties();
        FileInputStream in = null;
        in = new FileInputStream(fileName+".properties");
        try {
            props.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(SafeFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        //lesen aus der propsdatei und gleichzeitiges erstellen des savefile-objekts
        SaveFile sf = new SaveFile();
        sf.setHeight(Integer.parseInt(props.getProperty("height")));
        sf.setWidth(Integer.parseInt(props.getProperty("width")));
        sf.setLeben(Integer.parseInt(props.getProperty("leben")));
        sf.setSchluessel(Integer.parseInt(props.getProperty("schluessel")));
        sf.setSpielerX(Integer.parseInt(props.getProperty("spielerX")));
        sf.setSpielerY(Integer.parseInt(props.getProperty("spielerY")));
        Objekt[][] spielfeld;
        spielfeld = new Objekt[sf.getWidth()][sf.getHeight()];
        for(int i = 0; i < sf.getWidth(); i++){
            for(int j = 0; j < sf.getHeight(); j++){
                String testKey = Integer.toString(i) + "," + Integer.toString(j);
                if(props.containsKey(testKey)){
                    switch(Integer.parseInt(props.getProperty(testKey))){
                        case 0:
                            spielfeld[i][j] = new Wand();
                            break;
                        case 1:
                            spielfeld[i][j] = new Eingang();
                            break;
                        case 2:
                            spielfeld[i][j] = new Ausgang();
                            break;
                        case 4:
                            spielfeld[i][j] = new StatischesHindernis();
                            break;
                        case 5:
                            spielfeld[i][j] = new DynamischesHindernis();
                            break;
                        case 3:
                            spielfeld[i][j] = new Schlüssel();
                            break;
                    }
                }
            }
        }
        sf.setSpielfeld(spielfeld);
        return sf;
    }
}