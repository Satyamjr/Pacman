import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y, width, height;
        int startX, startY;
        int velocityX = 0, velocityY = 0;
        char direction = 'U';
        Image image, originalImage;
        boolean isPowerPellet = false;
        boolean isFrightened = false;
        int frightenedTimer = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.originalImage = image;
            this.x = x; this.y = y; this.width = width; this.height = height;
            this.startX = x; this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            switch (direction) {
                case 'U' -> { velocityX = 0; velocityY = -tileSize / 4; }
                case 'D' -> { velocityX = 0; velocityY = tileSize / 4; }
                case 'L' -> { velocityX = -tileSize / 4; velocityY = 0; }
                case 'R' -> { velocityX = tileSize / 4; velocityY = 0; }
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21, columnCount = 19, tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O    *bpo*     *   O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   *   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };


    HashSet<Block> walls, foods, ghosts;
    Block pacman;

    Timer gameLoop;
    Random random = new Random();
    int score = 0, lives = 3;
    boolean gameOver = false;
    boolean isGameStarted = false, isPaused = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        gameLoop = new Timer(50, this); // 20 FPS
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (ch == 'X') walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                else if (ch == 'b') ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'o') ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'p') ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'r') ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'P') pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                else if (ch == ' ') foods.add(new Block(null, x + 14, y + 14, 4, 4));
                else if (ch == '*') {
                    Block pellet = new Block(null, x + 10, y + 10, 12, 12);
                    pellet.isPowerPellet = true;
                    foods.add(pellet);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!isGameStarted) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("Press ENTER to Start", boardWidth / 2 - 150, boardHeight / 2);
            return;
        }

        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        for (Block wall : walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        g.setColor(Color.WHITE);
        for (Block food : foods)
            g.fillRect(food.x, food.y, food.width, food.height);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Lives: " + lives + "   Score: " + score, 10, 20);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Game Over! Press ENTER to Restart", boardWidth / 2 - 200, boardHeight / 2);
        } else if (isPaused) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("PAUSED", boardWidth / 2 - 60, boardHeight / 2);
        }
    }

    public void move() {
        if (!isGameStarted || isPaused) return;

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        if (pacman.x < 0) pacman.x = boardWidth - tileSize;
        else if (pacman.x + pacman.width > boardWidth) pacman.x = 0;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += food.isPowerPellet ? 50 : 10;
                if (food.isPowerPellet) {
                    for (Block ghost : ghosts) {
                        ghost.isFrightened = true;
                        ghost.frightenedTimer = 100;
                        ghost.image = blueGhostImage;
                    }
                }
            }
        }
        foods.remove(foodEaten);

        for (Block ghost : ghosts) {
            chasePacman(ghost);
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            if (ghost.x < 0) ghost.x = boardWidth - tileSize;
            else if (ghost.x + ghost.width > boardWidth) ghost.x = 0;

            if (collision(ghost, pacman)) {
                if (ghost.isFrightened) {
                    score += 200;
                    ghost.reset();
                    ghost.isFrightened = false;
                    ghost.image = ghost.originalImage;
                } else {
                    lives--;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
            }

            if (ghost.isFrightened) {
                ghost.frightenedTimer--;
                if (ghost.frightenedTimer <= 0) {
                    ghost.isFrightened = false;
                    ghost.image = ghost.originalImage;
                }
            }
        }

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public void chasePacman(Block ghost) {
        if (ghost.x % tileSize != 0 || ghost.y % tileSize != 0) return;
        char[] dirs = {'U', 'D', 'L', 'R'};
        int bestDist = Integer.MAX_VALUE;
        char bestDir = ghost.direction;
        for (char d : dirs) {
            if ((d == 'U' && ghost.direction == 'D') ||
                    (d == 'D' && ghost.direction == 'U') ||
                    (d == 'L' && ghost.direction == 'R') ||
                    (d == 'R' && ghost.direction == 'L')) continue;
            int nx = ghost.x, ny = ghost.y;
            if (d == 'U') ny -= tileSize;
            if (d == 'D') ny += tileSize;
            if (d == 'L') nx -= tileSize;
            if (d == 'R') nx += tileSize;
            Block test = new Block(null, nx, ny, tileSize, tileSize);
            boolean blocked = false;
            for (Block wall : walls) {
                if (collision(test, wall)) {
                    blocked = true;
                    break;
                }
            }
            if (blocked) continue;
            int dist = Math.abs(pacman.x - nx) + Math.abs(pacman.y - ny);
            if (dist < bestDist) {
                bestDist = dist;
                bestDir = d;
            }
        }
        ghost.updateDirection(bestDir);
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
                a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = pacman.velocityY = 0;
        for (Block g : ghosts) {
            g.reset();
            g.updateDirection('U');
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (!isGameStarted && e.getKeyCode() == KeyEvent.VK_ENTER) {
            isGameStarted = true;
            gameLoop.start();
        }

        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            loadMap();
            resetPositions();
            lives = 3; score = 0;
            gameOver = false;
            isGameStarted = true;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_P && isGameStarted) {
            isPaused = !isPaused;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
            pacman.image = pacmanUpImage;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
            pacman.image = pacmanDownImage;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
            pacman.image = pacmanLeftImage;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
            pacman.image = pacmanRightImage;
        }
    }
}










