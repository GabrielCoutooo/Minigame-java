package zeldaminiclone;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {
    public static int WIDTH, HEIGHT;
    public static int EnemiesRemaining;
    public static Player player;
    private World world;
    public static List<Enemy> enemies = new ArrayList<Enemy>();
    public static List<EnemyAmmo> enemyammo = new ArrayList<EnemyAmmo>();
    public static boolean gameOver = false;
    public static boolean playerLost = false;
    public static boolean enemiesDefeated = false;
    private static boolean isFullscreen = true;
    private static JFrame frame;
    private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    public static Game gameInstance;
    private volatile boolean gameRunning = false;
    private BufferStrategy bs;
    public static final int WORLD_WIDTH = 3840;
    public static final int WORLD_HEIGHT = 2160;
    public static Camera camera;
    private Menu menu;
    private boolean inMenu = true;
    private AudioManager audioManager;
    private static boolean gameOverMusicPlayed = false;

    // Atualizando Câmera para acompanhar o jogo em modo janela
    public static void updateCameraSize(int width, int height) {
        if (camera != null) {
            camera.updateScreenSize(width, height);
        }
    }

    // Geter statico para World
    public static World getWorld() {
        if (gameInstance == null) {
            throw new IllegalStateException("Game instance not initialized");
        }
        return gameInstance.world;
    }

    public static Game getInstance() {
        return gameInstance;
    }

    public Game() {
        gameInstance = this;
        // Detectando a resolução nativa do monitor do usuario
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
        // Adicionando eventos de teclado
        this.addKeyListener(this);
        // Adicionando eventos do mouse
        this.addMouseListener(this);
        // Adicionando Focus Listener para lidar
        // Implementando Altura e Largura
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        //Inicializando o AudioManager
        audioManager = new AudioManager();
        // Iniciando o player(Posição Inicial)
        new Spritesheet();
        player = new Player(32, 32,audioManager);
        world = new World();
        InitializingEnemies();
        camera = new Camera(WIDTH, HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        updateCameraSize(WIDTH, HEIGHT);
        // Inicializando o MENU
        menu = new Menu();
        inMenu = true;
    }

    public synchronized void start() {
        if (gameRunning)
            return;
        gameRunning = true;
        createBufferStrategy(3);
        bs = getBufferStrategy();
        Thread loop = new Thread(this, "game-loop");
        loop.setDaemon(true);
        loop.start();
    }

    public synchronized void stop() {
        gameRunning = false;
    }

    public void checkGameOver() {
        if (gameOver)
            return;

        if (player == null)
            return;

        if (player.health <= 0 && !gameOver) {
            	playerLost = true;
            	gameOver = true;
            	audioManager.stopMusic();
            	audioManager.playEffect("/audio/gameOverLost.wav");
        }

        if (enemies.isEmpty() && !enemiesDefeated) {
            enemiesDefeated = true;
            playerLost = false;
            gameOver = true;
            	audioManager.stopMusic();
            	audioManager.playEffect("/audio/gameOverWin.wav");
        }
    }

    // Posição dos inimigos
    private void InitializingEnemies() {
        // Criando objeto Random
        Random random = new Random();
        // Definindo altura/largura do inimigo
        int enemyWidth = 32;
        int enemyHeight = 32;
        int safeDistanceFromPlayer = 128;
        int safeDistanceBetweenEnemies = 64;
        // Limpando inimigos e projeteis antigos
        enemies.clear();
        enemyammo.clear();
        // Limites do Mapa baseado nos blocos

        int mapWidth = WIDTH;
        int mapHeight = HEIGHT;
        List<Rectangle> spawnedEnemies = new ArrayList<>();
        int numEnemies = 12;
        for (int i = 0; i < numEnemies; i++) {
            int x, y;
            Rectangle enemyRect;
            boolean tooCloseToPlayer;
            boolean collidesWithEnemy;
            int attempts = 0; // contador para evitar loop infinito
            do {
                // Posições aleatórias dentro do limite do mapa
                x = random.nextInt(Math.max(1, mapWidth - enemyWidth));
                y = random.nextInt(Math.max(1, mapHeight - enemyHeight));
                enemyRect = new Rectangle(x, y, enemyWidth, enemyHeight);
                // Verificando se a posição esta muito próxima ao player
                tooCloseToPlayer = enemyRect.intersects(
                        new Rectangle(
                                player.x - safeDistanceFromPlayer / 2,
                                player.y - safeDistanceFromPlayer / 2,
                                enemyWidth + safeDistanceFromPlayer,
                                enemyHeight + safeDistanceFromPlayer));
                // Verificando se a posição está muito próxima de outro inimigo já spawnado
                collidesWithEnemy = false;
                // Percorrendo todos os inimigos ja spawnados
                for (Rectangle r : spawnedEnemies) {
                    if (enemyRect.intersects(
                            new Rectangle(
                                    r.x - safeDistanceBetweenEnemies / 2,
                                    r.y - safeDistanceBetweenEnemies / 2,
                                    enemyWidth + safeDistanceBetweenEnemies,
                                    enemyHeight + safeDistanceBetweenEnemies))) {
                        collidesWithEnemy = true;
                        break;
                    }
                }
                // Incrementando contador de tentativas
                attempts++;
                if (attempts > 1000)
                    break;// Evitando loop infinito caso não haja espaço suficiente no mapa
            } while (!world.isFree(x, y) || tooCloseToPlayer || collidesWithEnemy);
            spawnedEnemies.add(enemyRect);
            enemies.add(new Enemy(x, y,audioManager));
        }
    }

    // Tick do jogo
    public void tick() {
    	if(inMenu) return;
        if (!gameOver) {
            // Adicionando o player ao tick
            player.tick();
            camera.update(player.x, player.y);
            // Adicionando os inimigos ao tick
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).tick();
            }
            // Implementação da ammo inimiga
            for (int i = 0; i < enemyammo.size(); i++) {
                enemyammo.get(i).tick();
            }
            checkGameOver();
        }
    }

    private void renderGameOverScreen01(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String message = "Parabéns todos os inimigos foram derrotados!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (WIDTH - messageWidth) / 2, HEIGHT / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartMessage = "Pressione 'R' para reiniciar o jogo!";
        int restartWidth = g.getFontMetrics().stringWidth(restartMessage);
        g.drawString(restartMessage, (WIDTH - restartWidth) / 2, HEIGHT / 2 + 20);
        String exitMessage = "Pressione 'Esc' para voltar ao menu do jogo";
        int exitWidth = g.getFontMetrics().stringWidth(exitMessage);
        g.drawString(exitMessage, (WIDTH - exitWidth) / 2,HEIGHT / 2 + 50);

    }

    private void renderGameOverScreen02(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String message = "Você Morreu Treine Mais!";
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (WIDTH - messageWidth) / 2, HEIGHT / 2 - 20);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String restartMessage = "Pressione 'R' para tentar novamente!";
        int restartWidth = g.getFontMetrics().stringWidth(restartMessage);
        g.drawString(restartMessage, (WIDTH - restartWidth) / 2, HEIGHT / 2 + 20);
        String exitMessage = "Pressione 'Esc' para voltar ao menu do jogo";
        int exitWidth = g.getFontMetrics().stringWidth(exitMessage);
        g.drawString(exitMessage, (WIDTH - exitWidth) / 2,HEIGHT / 2 + 50);

    }

    // Renderização
    public void render() {
        // Se BufferStrategy não existe, tenta recriar
        if (bs == null) {
            if (!safeRecreateBufferStrategy()) {
                System.out.println("Não é possível renderizar - BufferStrategy null");
                return;
            }
        }

        // Se componente não está pronto
        if (!isDisplayable()) {
            System.out.println("Componente não está displayable");
            return;
        }

        Graphics g = null;
        try {
            g = bs.getDrawGraphics();
            //Renderizando o MENU
        	if(inMenu) {
        		menu.render(g, WIDTH, HEIGHT);
        	} else {
            // Fundo preto antes de desenhar
            g.setColor(Color.black);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Renderiza tiles do chão (madeira)
            for (int x = 0; x < WIDTH; x += 16) {
                for (int y = 0; y < HEIGHT; y += 16) {
                    g.drawImage(Spritesheet.wood, x, y, null);
                }
            }
            // Deslocando a câmera
            g.translate(-camera.x, -camera.y);
            // Renderizando entidades
            world.render(g, camera);
            player.render(g);

            for (Enemy enemy : enemies) {
                enemy.render(g);
            }
            for (EnemyAmmo ammo : enemyammo) {
                ammo.render(g);
            }
            // Desfazendo o deslocamento para renderizar Game Over
            g.translate(camera.x, camera.y);
            // Tela de Game Over
            if (gameOver) {
                if (playerLost) {
                    renderGameOverScreen02(g);
                } else if (enemiesDefeated) {
                    renderGameOverScreen01(g);
                	}
            	}
        	}

        } catch (Exception e) {
            System.out.println("Erro durante renderização: " + e.getMessage());
            bs = null;
        } finally {
            if (g != null)
                g.dispose();
        }

        // Mostrando buffer
        try {
            bs.show();
        } catch (IllegalStateException | NullPointerException e) {
            System.out.println("Problema ao exibir BufferStrategy: " + e.getMessage());
            bs = null;
        }

        Toolkit.getDefaultToolkit().sync();
    }

    // Reiniciando o Jogo apos o Game OVER
    private void restartGame() {
        gameOver = false;
        playerLost = false;
        enemiesDefeated = false;
        gameOverMusicPlayed = false;
        player = new Player(32, 32, audioManager);
        enemies.clear();
        enemyammo.clear();
        InitializingEnemies();
        audioManager.playMusic("/audio/gameplayMusic.wav");
    }

    // Main
    public static void main(String[] args) {
        Game game = new Game();
        gameInstance = game;
        frame = new JFrame();
        frame.add(game);
        // Fullscreen
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setTitle("Minigame");
        // Para quando o Java for finalizado o JFrame tambem ser
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // Mostrando o JFrame
        frame.setVisible(true);
        // Iniciando em fullscreen
        device.setFullScreenWindow(frame);
        game.start();
    }

    // Game loop
    @Override
    public void run() {
    	final double NS_PER_SECOND = 1_000_000_000.0;
    	final double TARGET_FPS = 60.0;
    	final double TIME_PER_TICK = NS_PER_SECOND / TARGET_FPS;
    	
    	long lastTime = System.nanoTime();
    	double delta = 0;
    	
    	while(gameRunning) {
    		long now = System.nanoTime();
    		delta += (now - lastTime) / TIME_PER_TICK;
    		lastTime = now;
    		
    		while (delta >= 1) {
    			tick();
    			delta--;
    		}
    		if(isDisplayable()) {
    			render();
    		}
    	}
    }



    public void forceRender() {
        if (bs != null) {
            try {
                Graphics g = bs.getDrawGraphics();
                if (g != null) {
                    // Renderizar conteúdo mínimo
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, WIDTH, HEIGHT);
                    g.setColor(Color.WHITE);
                    g.drawString("Loading...", WIDTH / 2 - 30, HEIGHT / 2);
                    g.dispose();
                    bs.show();
                }
            } catch (Exception e) {
                System.out.println("Force render failed: " + e.getMessage());
            }
        }
    }

    // Método para recriar BufferStrategy
    public synchronized boolean safeRecreateBufferStrategy() {
        try {
            if (bs != null) {
                try {
                    bs.dispose();
                } catch (Exception ignored) {
                }
                bs = null;
            }

            if (isDisplayable()) {
                createBufferStrategy(3);
                bs = getBufferStrategy();
                return bs != null;
            }
        } catch (Exception e) {
            System.out.println("Erro em safeRecreateBufferStrategy: " + e.getMessage());
        }
        return false;
    }

    private static void toggleFullscreen() {
        if (frame == null)
            return;

        System.out.println("Iniciando toggleFullscreen...");

        Runnable task = () -> {
            try {
                // Liberando a BufferStrategy antiga
                try {
                    BufferStrategy oldBS = gameInstance.getBufferStrategy();
                    if (oldBS != null)
                        oldBS.dispose();
                } catch (Exception ignored) {
                }
                gameInstance.bs = null;

                // Alternando o estado
                isFullscreen = !isFullscreen;

                // Libera fullscreen atual
                device.setFullScreenWindow(null);

                // Fecha frame antigo
                frame.dispose();
                JFrame newFrame = new JFrame();
                newFrame.setTitle("Minigame");
                newFrame.setResizable(false);
                newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // Ajusta dimensões
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                if (isFullscreen) {
                    WIDTH = screenSize.width;
                    HEIGHT = screenSize.height;
                    newFrame.setUndecorated(true);
                } else {
                    WIDTH = screenSize.width / 2;
                    HEIGHT = screenSize.height / 2;
                    newFrame.setUndecorated(false);
                    newFrame.setSize(WIDTH, HEIGHT);
                    newFrame.setLocationRelativeTo(null);
                }

                // Atualizando apenas a câmera com novo tamanho de tela
                updateCameraSize(WIDTH, HEIGHT);

                // Atualizando Canvas
                gameInstance.setPreferredSize(new Dimension(WIDTH, HEIGHT));
                newFrame.add(gameInstance);

                // Ativando janela
                newFrame.pack();
                newFrame.setVisible(true);
                if (isFullscreen) {
                    device.setFullScreenWindow(newFrame);
                }

                frame = newFrame;

                // Recriando BufferStrategy
                try {
                    gameInstance.createBufferStrategy(3);
                    gameInstance.bs = gameInstance.getBufferStrategy();
                } catch (Exception e) {
                    System.out.println("Erro ao criar BufferStrategy: " + e.getMessage());
                    gameInstance.bs = null;
                }

                // Forçando foco e render inicial
                gameInstance.requestFocusInWindow();
                gameInstance.render();

                System.out.println("Toggle completado: " +
                        (isFullscreen ? "Fullscreen" : "Janela") +
                        " " + WIDTH + "x" + HEIGHT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        SwingUtilities.invokeLater(task);
    }

    // Implementação do KeyListener
    public void keyTyped(KeyEvent e) {
    }
    
    // Setando as teclas de movimentação do Player
    public void keyPressed(KeyEvent e) {
    	// Reiniciando o Jogo caso de Game OVER
    	if (gameOver) {
    		if (e.getKeyCode() == KeyEvent.VK_R) {
    			restartGame();
    		}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
    			gameOver = false;
    			playerLost = false;
    			enemiesDefeated = false;
    			inMenu = true;
    			//Tocando a musica do menu
    			if(audioManager != null) {
    				audioManager.playMusic("/audio/menuMusic.wav");
    			}
    		}
    		return; // Não deixa o player mover após o game over
    	}
    	if (e.isAltDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
    		toggleFullscreen();
    		return;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_D) {
    		player.right = true;
    	} else if (e.getKeyCode() == KeyEvent.VK_A) {
    		player.left = true;
    	}
    	if (e.getKeyCode() == KeyEvent.VK_W) {
    		player.up = true;
    	} else if (e.getKeyCode() == KeyEvent.VK_S) {
    		player.down = true;
    	}
    	
    }
    // Garantindo que a ação do movimento pare quando a tecla não for mais ativada
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.right = false;
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            player.left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;
        }

    }

    // Implementação do Mouse Listener
    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
    	int mx = e.getX();
    	int my = e.getY();
    	if (inMenu) {
    		if(menu.clickStart(e.getX(), e.getY(), WIDTH, HEIGHT)) {
    			inMenu = false;
    			audioManager.playMusic("/audio/gameplayMusic.wav");
    		}else if(menu.clickExit(e.getX(), e.getY(), WIDTH, HEIGHT)) {
    			System.exit(0);
    		}else {
    		}
    		return;
    	}
        if (gameOver)
            return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            player.shoot = true;
        }
    }

    // Garantindo que a ação pare quando o mouse1 for solto
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            player.shoot = false;
        }
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

}
