package zeldaminiclone;

public class Camera {
	public int x,y;
	private int maxX,maxY;
	private int screenWidth,screenHeight;
	
	
	public Camera(int screenWidth,int screenHeight,int worldWidth,int worldHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.maxX = worldWidth - screenWidth;
		this.maxY = worldHeight - screenHeight;
	}
	public int getWidth() {
		return screenWidth;
	}
	public int getHeight() {
		return screenHeight;
	}
	
	public void update(int playerX,int playerY) {
		// Centralizando a câmera no Player
		x = playerX - screenWidth / 2;
		y = playerY - screenHeight / 2;
		//Impedindo que a camera vá além dos limites do mapa
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		if(x > maxX) x = maxX;
		if(y > maxY) y = maxY;
		
	}
	public void updateScreenSize(int newScreenWidth, int newScreenHeight) {
	    this.screenWidth = newScreenWidth;
	    this.screenHeight = newScreenHeight;
	    this.maxX = Game.WORLD_WIDTH - newScreenWidth;
	    this.maxY = Game.WORLD_HEIGHT - newScreenHeight;
	}
}
