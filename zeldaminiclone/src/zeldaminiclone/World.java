package zeldaminiclone;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    public List<Blocks> blocks = new ArrayList<>();
    private Random random = new Random();

    public World() {
        int worldWidth = Game.WORLD_WIDTH;    // largura fixa do mapa
        int worldHeight = Game.WORLD_HEIGHT;  // altura fixa do mapa
        int horizontalBlocks = worldWidth / 32;
        int verticalBlocks = worldHeight / 32;
        int safeZoneSize = 5 * 32;// (5 blocos de largura e altura)
        Rectangle safeZone = new Rectangle(Game.player.x - 32, Game.player.y - 32,safeZoneSize,safeZoneSize);
        

        // Bordas do mapa
        for (int xx = 0; xx < horizontalBlocks; xx++) {
            blocks.add(new Blocks(xx * 32, 0));                  // superior
            blocks.add(new Blocks(xx * 32, worldHeight - 32));   // inferior
        }
        for (int yy = 0; yy < verticalBlocks; yy++) {
            blocks.add(new Blocks(0, yy * 32));                  // esquerda
            blocks.add(new Blocks(worldWidth - 32, yy * 32));    // direita
        }

        // Estruturas fixas
        // Parede horizontal no terço superior
        for (int xx = 0; xx < 5; xx++) {
            blocks.add(new Blocks(worldWidth / 2 + (xx * 32), worldHeight / 3));
        }

        // Parede vertical no terço esquerdo
        for (int yy = 0; yy < 5; yy++) {
            blocks.add(new Blocks(worldWidth / 4, worldHeight / 2 + (yy * 32)));
        }

        // Obstáculos aleatórios (~10% da tela)
        int numRandomBlocks = (horizontalBlocks * verticalBlocks) / 10;
        for (int i = 0; i < numRandomBlocks; i++) {
            int randX = random.nextInt(horizontalBlocks) * 32;
            int randY = random.nextInt(verticalBlocks) * 32;
            Rectangle blockRect = new Rectangle(randX, randY, 32, 32);
            if(blockRect.intersects(safeZone)) {
            	continue;
            }

            // Evita spawn do player e paredes fixas
            if ((randX >= worldWidth / 2 && randX < worldWidth / 2 + 5*32 &&
            	     randY == worldHeight / 3) ||
            	    (randX == worldWidth / 4 &&
            	     randY >= worldHeight / 2 && randY < worldHeight / 2 + 5*32)) {
            	    continue;
            	}

            blocks.add(new Blocks(randX, randY));
        }
    }

    // Colisão
    public boolean isFree(int x, int y) {
        for (Blocks block : blocks) {
            if (block.intersects(new Rectangle(x, y, 32, 32))) {
                return false;
            }
        }
        return true;
    }

    // Renderização(Obs renderizando apenas o que é mostrado ao Player ou seja a câmera)
    public void render(Graphics g, Camera camera) {
        for (Blocks block : blocks) {
        	if(block.x + 32 > camera.x && block.x < camera.x + camera.getWidth() && block.y + 32 > camera.y && block.y < camera.y + camera.getHeight()) {
        		block.render(g);
        	}
        }
    }
}
