package zeldaminiclone;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Menu {
    private BufferedImage background;
    private int originalWidth = 1280;  
    private int originalHeight = 720;
    private AudioManager audio;

    // Coordenadas do botão START
    private Rectangle originalStartButton = new Rectangle(421, 359, 580, 168);

    // Coordenadas do botão EXIT 
    private Rectangle originalExitButton = new Rectangle(418, 564, 583, 162);

    public Menu() {
    	audio = new AudioManager();
    	audio.playMusic("/audio/menuMusic.wav");
        try {
            background = ImageIO.read(getClass().getResource("/menu.png"));
            if (background != null) {
                originalWidth = background.getWidth();
                originalHeight = background.getHeight();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g, int screenWidth, int screenHeight) {
        g.drawImage(background, 0, 0, screenWidth, screenHeight, null);
    }

    public Rectangle getStartButton(int screenWidth, int screenHeight) {
        return calculateScaledButton(originalStartButton, screenWidth, screenHeight);
    }

    public Rectangle getExitButton(int screenWidth, int screenHeight) {
        return calculateScaledButton(originalExitButton, screenWidth, screenHeight);
    }

    private Rectangle calculateScaledButton(Rectangle originalButton, int screenWidth, int screenHeight) {
        float scaleX = (float) screenWidth / originalWidth;
        float scaleY = (float) screenHeight / originalHeight;

        int x = (int) (originalButton.x * scaleX);
        int y = (int) (originalButton.y * scaleY);
        int width = (int) (originalButton.width * scaleX);
        int height = (int) (originalButton.height * scaleY);

        return new Rectangle(x, y, width, height);
    }

    public boolean clickStart(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        Rectangle btn = getStartButton(screenWidth, screenHeight);
        boolean clicked = btn.contains(mouseX,mouseY);
        if(clicked) {
        	audio.stopMusic();
        }
        return clicked;
    }

    public boolean clickExit(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        Rectangle btn = getExitButton(screenWidth, screenHeight);
        boolean clicked = btn.contains(mouseX,mouseY);
        if(clicked) {
        	audio.stopMusic();
        }
        return clicked;
    }
}
