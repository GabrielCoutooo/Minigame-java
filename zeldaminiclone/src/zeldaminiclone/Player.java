package zeldaminiclone;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
public class Player extends Rectangle{
	public int health = 100;
	public int spd = 4;
	public boolean right,up,down,left;
	public int curAnimation = 0;// cur = current
	public int curFrames = 0, targetFrames = 15;
	public static List<Ammo> ammo = new ArrayList<Ammo>();
	public boolean shoot = false;
	public int direction = 1;
	public boolean isHit = false;
	private final int MAX_HIT_FRAMES = 60; // duração do efeito (1s a 60fps)
	public int hitTimer = 0;
	private AudioManager audioManager;
	public void TakeDamage(int damage) {
		health-=damage;
		if(health <= 0) {
			health = 0 ;
		}
		isHit = true;
		hitTimer = MAX_HIT_FRAMES;
	}
	public Player(int x, int y, AudioManager audioManager){
		super(x,y,32,32);
		this.audioManager = audioManager;
		
	}
	public void tick() {
		boolean moved = false;
		//Movimentação do PLayer
		if(right && Game.getWorld().isFree(x + spd, y)) {
			x += spd;
			moved = true;
			direction = 1;
		}else if(left && Game.getWorld().isFree(x-spd, y)) {
			x -= spd;
			moved = true;
			direction = -1;
		}
		if(up && Game.getWorld().isFree(x, y-spd)) {
			y -= spd;
			moved = true;
		}else if(down && Game.getWorld().isFree(x, y+spd)){
			y += spd;
			moved = true;
		}
		//Animação do jogador
		if(moved) {
			curFrames ++;
			if(curFrames == targetFrames) {
				curFrames = 0;
				curAnimation ++;
			if(curAnimation == Spritesheet.player_front.length) {
				curAnimation = 0;
				}
			}
		}
		//Criando a munição
		if(shoot) {
			shoot = false;
			ammo.add(new Ammo(x,y,direction));
			audioManager.playEffect("/audio/playerAttack.wav");
		}
		// Atualizando o tick das munições
		for(int i = 0; i<ammo.size();i++) {
			ammo.get(i).tick();
		}
		//Controle do hit
		if(isHit) {
			hitTimer--;
			if(hitTimer <=0) {
				isHit = false;
			}
		}
	}
	public void render(Graphics g) {

		if(isHit) {
			if((hitTimer / 10) % 2 == 0) {
				g.drawImage(Spritesheet.player_hit,x,y,32,32, null);
		}else {
			g.drawImage(Spritesheet.player_front[curAnimation],x,y,32,32,null );
		}
		}else {
			g.drawImage(Spritesheet.player_front[curAnimation],x,y,32,32,null );
		}
		//Exibindo a barra de vida
		g.setColor(Color.red);
		g.fillRect(x, y - 10, 32, 5);
		g.setColor(Color.green);
		 g.fillRect(x, y - 10, (int) (32 * (health / 100.0)), 5);
		// Renderizando a Ammo
		for(int i = 0; i<ammo.size();i++) {
			ammo.get(i).render(g);
		}
	}
}
