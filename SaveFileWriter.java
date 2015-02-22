/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Wird dazu benutzt um mit einem gegebenen SafeFileObjekt eine entsprechende PropertyDatei zu schreiben
 * @author Frausig
 */
public class SaveFileWriter {
    
    public static void createSaveFile(SaveFile sf, String fileName){
        Properties props = new Properties();
        props.setProperty("spielerX", Integer.toString(sf.getSpielerX()));
        props.setProperty("spielerY", Integer.toString(sf.getSpielerY()));
        props.setProperty("leben", Integer.toString(sf.getLeben()));
        props.setProperty("schluessel", Integer.toString(sf.getSchluessel()));
        props.setProperty("width", Integer.toString(sf.getWidth()));
        props.setProperty("height", Integer.toString(sf.getHeight()));
        Objekt[][] spielfeld = sf.getSpielfeld();
        for(int i = 0; i < sf.getWidth(); i++){
            for(int j = 0; j < sf.getHeight(); j++){
                if(spielfeld[i][j] != null){
                    props.setProperty(i+","+j, Integer.toString(spielfeld[i][j].identifier));
                }
            }
        }
        try{
            
            FileOutputStream outFile = new FileOutputStream(new File(fileName+".properties"));
            props.store(outFile, fileName);
            outFile.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}