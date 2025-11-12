package zeldaminiclone;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;

public class Ammo extends Rectangle{
	public int direction = 1;
	public int speed = 8;
	public int frames = 0;
	public Ammo(int x,int y,int direction) {
		super(x,y,20,20);
		this.direction = direction;
	}
	public void tick() {
		x += speed*direction;
		//Criando Colisão com a parede
		for(Blocks block : Game.getWorld().blocks) {
			if(this.intersects(block)) {
				removeThisAmmo();
				return;
			}
		}
		//Dano ao inimigo
        for (Enemy enemy : Game.enemies) {
            if (this.intersects(enemy)) {
                enemy.TakeDamage(10);  
                Player.ammo.remove(this);  
                break;
            }
        }
		//Criando Colisão com os inimigos
		Iterator<Ammo> iterator = Player.ammo.iterator();
		while (iterator.hasNext()) {
		    Ammo ammo = iterator.next();
		    for (Enemy enemy : Game.enemies) {
		        if (ammo.intersects(enemy)) {
		        	enemy.isHit = true;
		        	enemy.TakeDamage(10);
		            iterator.remove(); // Remove a ammo
		            break; // Para não continuar verificando a colisão com outros inimigos
		        }
		    }
		}
		// Parando de renderizar o disparo depois de 2 segundos(120ticks)para otimização
		frames++;
		if(frames == 120) {
			Player.ammo.remove(this);
			return;
		}
	}
		private void removeThisAmmo() {
			Iterator<Ammo> iterator = Player.ammo.iterator();
			while(iterator.hasNext()) {
				Ammo ammo = iterator.next();
				if(ammo == this) {
					iterator.remove();
					break;
				}
			}
		}
	public void render(Graphics g) {
		g.drawImage(Spritesheet.fireBall, x, y,26,26,null);
	}
}
	
