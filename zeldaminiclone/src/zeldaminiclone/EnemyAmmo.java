package zeldaminiclone;

import java.awt.Graphics;
import java.awt.Rectangle;

public class EnemyAmmo extends Rectangle {
    public double dx, dy;
    public int speed = 6;
    private int frames = 0;

    // Construtor para criar a munição do inimigo
    public EnemyAmmo(int x, int y, double dx, double dy) {
        super(x, y, 26, 26);
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }
    public void tick() {
        // Move o projétil
        x += dx * speed;
        y += dy * speed;
        
		//Criando Colisão com a parede
        for(Blocks block : Game.getInstance().getWorld().blocks) {
			if(this.intersects(block)) {
				Game.enemyammo.remove(this);
				return;
			}
		}
        // Verifica colisão com o jogador
        if ((this.dx != 0 || this.dy != 0)&& this.intersects(Game.player)) {
            Game.player.TakeDamage(20);
            Game.enemyammo.remove(this);
            return;
        }

        // Verifica se o projétil saiu da tela
        if (x < 0 || y < 0 || x > Game.WIDTH || y > Game.HEIGHT) {
            Game.enemyammo.remove(this); // Remove o projétil quando sair da tela
            return;
        }

        // Incrementa o contador de frames 
        frames++;
        if (frames > 240) {  //240 frames = 4 segundos
            Game.enemyammo.remove(this); 
        }
    }

    // Renderiza o projétil
    public void render(Graphics g) {
    	if(dx != 0 || dy != 0) {
    		g.drawImage(Spritesheet.BlueFireball, x, y, 26, 26, null);
    	}
        if(speed == 0) {
        	Game.enemyammo.remove(this);
        }
    }
    	
}