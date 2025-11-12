package zeldaminiclone;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
//Setando as sprites utilizadas
public class Spritesheet {
	//Declaração de Entidades
	public static BufferedImage spritesheet;
	public static BufferedImage player_front[];
	public static BufferedImage player_hit;
	public static BufferedImage tileWall;
	public static BufferedImage wood;
	public static BufferedImage fireBall;
	public static BufferedImage Enemy01_front[];
	public static BufferedImage Enemy02;
	public static BufferedImage Enemyhit;
	public static BufferedImage BlueFireball;
	// Carregando Spritesheet
	public Spritesheet() {
		try {
			spritesheet = ImageIO.read(getClass().getResource("/spritesheet.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Pegando as coordenadas dos sprites na spritesheet
		player_front = new BufferedImage[2];
		player_front[0] = Spritesheet.getSprite(0, 11, 16, 16);
		player_front[1] = Spritesheet.getSprite(16, 11, 16, 16);
		player_hit = Spritesheet.getSprite(75, 258, 16, 16);
		tileWall = Spritesheet.getSprite(315, 185, 16, 16);
		fireBall = Spritesheet.getSprite(191, 185, 16, 16);
		BlueFireball = Spritesheet.getSprite(190, 204, 16, 16);
		Enemy01_front = new BufferedImage[2];
		Enemy01_front[0] = Spritesheet.getSprite(136, 206, 16, 16);
		Enemy01_front[1] = Spritesheet.getSprite(154, 206, 16, 16);
		wood = Spritesheet.getSprite(335, 185, 16, 16);
		Enemyhit = Spritesheet.getSprite(58, 224, 16, 16);
		
	}
	public static BufferedImage getSprite(int x,int y,int width,int height) {
		return spritesheet.getSubimage(x, y, width, height);
	}
}
