/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weihnachtsprojekt;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;

/**
 * KLasse stellt ein einfaches Verwaltungsobjekt dar für AudioClips aus der API
 * @author Frausig
 */
public class AudioPlayer {

    private String name;
    private AudioClip sound;

    public AudioPlayer() {
        
    }
    
    public void start(){
        sound.play();
 
    }
    
    public void setMusic(String name_){
        name = name_ +".wav";
        sound.play();
    }

    public void init(String name_) {
        name = name_ + ".wav";
        File f = new File(name);
        sound = null;
        try {
            sound = Applet.newAudioClip(f.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stop(){
        sound.stop();
    }
    
    public void loop(){
        sound.loop();
    }
}
