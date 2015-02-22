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
 * Die Klasse stellt ein Objekt dar, welches benutzt wird, um einen Level aus einer Property-Datei zu lesen
 * @author Frausig
 */
public class ConfigReader {
    private int height;
    private int width;
    private Objekt[][] spielfeld;
    
    public ConfigReader() throws FileNotFoundException{
        Properties props = new Properties();
        FileInputStream in = null;
        in = new FileInputStream("level.properties");
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        height = Integer.parseInt(props.getProperty("Height"));
        width = Integer.parseInt(props.getProperty("Width"));
        spielfeld = new Objekt[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
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
                        case 3:
                            spielfeld[i][j] = new StatischesHindernis();
                            break;
                        case 4:
                            spielfeld[i][j] = new DynamischesHindernis();
                            break;
                        case 5:
                            spielfeld[i][j] = new Schlüssel();
                            break;
                    }
                }
            }
        }
        
        System.out.println(height);
        System.out.println(width);
    }
    
    public Objekt[][] getSpielfeld(){
        return spielfeld;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
}
