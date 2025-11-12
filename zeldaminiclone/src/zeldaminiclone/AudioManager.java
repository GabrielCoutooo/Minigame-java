package zeldaminiclone;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioManager {

    private Clip musicClip;
    private Clip moveClip;

    public void playMusic(String resourcePath) {
        stopMusic();
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio não encontrado: " + resourcePath);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream);
            musicClip.start();
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (musicClip != null){
        	if(musicClip.isRunning()) {
        		musicClip.stop();
        	}
        	musicClip.close();
        	musicClip = null;
        } 
    }

    public void playEffect(String resourcePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio não encontrado: " + resourcePath);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playMovement(String resourcePath, boolean loop) {
        stopMovement();
        try {
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio não encontrado: " + resourcePath);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            moveClip = AudioSystem.getClip();
            moveClip.open(audioStream);
            moveClip.start();
            if (loop) {
                moveClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopMovement() {
        if (moveClip != null && moveClip.isRunning()) {
            moveClip.stop();
        }
    }
}
