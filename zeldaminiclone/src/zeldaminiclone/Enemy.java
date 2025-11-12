package zeldaminiclone;
import java.awt.Color;
import java.awt.Graphics;
import java. util.Random;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enemy extends Rectangle{
	private static final Random rand = new Random();
	public int health = 50;
	public int spd = 2;
	public int curAnimation = 0;// cur = current
	public int curFrames = 0, targetFrames = 15;
	public static List<Ammo> ammo = new ArrayList<Ammo>();
	public boolean shoot = false;
	public int direction = 1;
	public boolean isHit = false;
	public int hitFrames = 0;
	public static final int HitDuration = 30;
	private int shootCooldown = 30; 
	private int shootTimer = 0;
	private AudioManager audioManager;
	
	public Enemy(int x, int y, AudioManager audioManager){
		super(x,y,32,32);
		spd = 3 + rand.nextInt(2);
		this.audioManager = audioManager;
	}
	
	public void TakeDamage(int damage) {
		health -= damage;
		isHit = true;
		hitFrames = HitDuration;
		if(health <= 0 ) {
			if(audioManager != null) {
				audioManager.playEffect("/audio/enemyDeath.wav");
			}
			health = 0;
			Game.enemies.remove(this);
		}
	}
	
	private boolean checkForOtherEnemies(int newX, int newY) {
		for (Enemy e : Game.enemies) {
			if (e != this && e.getBounds().intersects(new Rectangle(newX, newY, width, height))) {
				return true;
			}
		}
		return false;
	}

	//Função para o Inimigo perseguir o Jogador
	public void chasePlayer() {
	    Player p = Game.player;
	    double dx = p.x - x;
	    double dy = p.y - y;
	    double distance = Math.sqrt(dx * dx + dy * dy);
	    if(distance != 0) {
	    	dx /= distance;
	    	dy /= distance;
	    }
	    int newX = x + (int) (dx * spd);
	    int newY = y + (int) (dy * spd);
	    
	    //Tentando movimentar na direção direta
	    if(tryMove(newX, newY)) return;
	    
	    //Tentando movimentar apenas horizontal ou vertical
	    if(tryMove(x + (int)(dx * spd), y)) return;
	    if(tryMove(x, y + (int)(dy * spd))) return;
	    
	    	//Se bloqueado tenta pequenos deslocamentos alternativos
	    	int[] offsets = {-spd, 0, spd};
	    	outer:
	    		for(int ox : offsets) {
	    			for(int oy : offsets) {
	    				if(ox == 0 && oy == 0) continue;
	    				if(tryMove(x + ox,y + oy)) break outer;
	    				}
	    			}
	    }
	
	private boolean tryMove(int targetX, int targetY) {
		if(Game.getWorld().isFree(targetX, targetY) && !checkForOtherEnemies(targetX, targetY)) {
			x = targetX;
			y = targetY;
			return true;
		}
		return false;
	}
	private void shootAtPlayer(int playerX,int playerY) {
        Player p = Game.player;
        double dx = p.x - x;
        double dy = p.y -y;
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        dx /= magnitude;
        dy /= magnitude;
        
        EnemyAmmo newAmmo = new EnemyAmmo(x + width / 2, y+ height/2,dx,dy);
        Game.enemyammo.add(newAmmo);
        if(audioManager != null) {
        	audioManager.playEffect("/audio/enemyShoot.wav");
        }
        shootCooldown = 40 + rand.nextInt(10);
        shootTimer = shootCooldown;
	}
	private boolean canSeePlayer() {
		int playerX = Game.player.x + Game.player.width / 2;
		int playerY = Game.player.y + Game.player.height /2;
		int enemyX = x + width / 2;
		int enemyY = y + height / 2;
		int steps = Math.max(Math.abs(playerX - enemyX), Math.abs(playerY - enemyY));
		double dx = (playerX - enemyX) / (double) steps;
		double dy = (playerY - enemyY) / (double) steps;
		double checkX = enemyX;
		double checkY = enemyY;
		for(int i = 0; i< steps;i++) {
			checkX += dx;
			checkY += dy;
			//Verificando colisão com blocos
			for(Blocks block : Game.getWorld().blocks) {
				if(block.contains((int) checkX, (int) checkY)) {
					return false;// Párede no caminho
				}
			}
		}
		return true; // Linha de visão livre
	}
	public void tick() {
		//Controlador de disparo
		if(shootTimer <= 0 && Math.abs(x - Game.player.x) < 200 && canSeePlayer()) {
			shootAtPlayer(Game.player.x,Game.player.y);
		}else {
			shootTimer --;
		}
		//Lógica de Dano
		if(isHit) {
			hitFrames--;
			if(hitFrames <= 0) {
				isHit = false;
				hitFrames = 0;
			}
		}else {
			chasePlayer();
		}
		//Animação do inimigo
		if(!isHit) {
			curFrames ++;
			if(curFrames >= targetFrames) {
				curFrames = 0;
				curAnimation ++;
			if(curAnimation >= Spritesheet.Enemy01_front.length) {
				curAnimation = 0;
				}
			}
		}else {
			curAnimation = 0;
		}
		//Criando a munição
		if(shoot) {
			shoot = false;
			ammo.add(new Ammo(x,y,direction));
		}
		//Atualizando o tick das munições com Iterator seguro
		Iterator<Ammo> ammoIterator = ammo.iterator();
		while(ammoIterator.hasNext()) {
			Ammo currentAmmo = ammoIterator.next();
			currentAmmo.tick();
		}
	}
	public void render(Graphics g) {
		//Exibindo a vida do inimigo
		if(health < 50) {
			g.setColor(Color.red);
			g.fillRect(x, y - 10, 32, 5); // Barra de fundo da vida
			g.setColor(Color.GREEN);
			g.fillRect(x, y - 10, (int) (32 * (health / 50.0)), 5); // Barra de vida
		}
		//Renderizando visualmete o dano tomado pelo inimigo
		if(isHit) {
			g.drawImage(Spritesheet.Enemyhit,x,y,32,32,null);
		}else {
			g.drawImage(Spritesheet.Enemy01_front[curAnimation],x,y,32,32,null);
		}
		// Renderizando a Ammo
		for(int i = 0; i<ammo.size();i++) {
			ammo.get(i).render(g);
		}
	}
}
