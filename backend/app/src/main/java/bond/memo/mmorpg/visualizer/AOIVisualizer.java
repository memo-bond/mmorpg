package bond.memo.mmorpg.visualizer;

import bond.memo.mmorpg.adapter.KeyListenerAdapter;
import bond.memo.mmorpg.aoi.AOISystem;
import bond.memo.mmorpg.aoi.AOISystemImpl;
import bond.memo.mmorpg.aoi.GridCell;
import bond.memo.mmorpg.model.Player;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static bond.memo.mmorpg.constants.Constants.RADIUS;

@Slf4j
public class AOIVisualizer extends JPanel {

    private final int gridSize;
    private final int cellSize;
    private final AOISystem aoiSystem;
    private final Player mainPlayer;
    private final List<Player> players;

    public static AOIVisualizer from(AOISystemImpl aoiSystem, Player mainPlayer) {
        return new AOIVisualizer(aoiSystem, mainPlayer);
    }

    public AOIVisualizer(AOISystemImpl aoiSystem, Player mainPlayer) {
        this.gridSize = aoiSystem.getGridSize();
        this.cellSize = aoiSystem.getCellSize();
        this.aoiSystem = aoiSystem;
        this.mainPlayer = mainPlayer;
        this.players = new CopyOnWriteArrayList<>();

        setPreferredSize(new Dimension(gridSize, gridSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
        Timer timer = new Timer(100, e -> {
            updatePlayerPositions();
            checkMainPlayerCollisions();
            repaint();
        });
        timer.start();

        // Key listener for main player movement
        addKeyListener(KeyListenerAdapter.adapter(this::controlMainPlayer));
        setFocusable(true);
    }

    private void checkMainPlayerCollisions() {
        if (mainPlayer != null) {
            for (Player player : players) {
                if (player != mainPlayer) {
                    double distance = Math.sqrt(
                            Math.pow(player.getPosition().getX() - mainPlayer.getPosition().getX(), 2) +
                                    Math.pow(player.getPosition().getY() - mainPlayer.getPosition().getY(), 2)
                    );
                    if (distance < RADIUS)
                        log.info("Collision detected between main player {} vs player {}", mainPlayer.getName(), player.getName());
                }
            }
        }
    }

    private void controlMainPlayer(KeyEvent e) {
        if (mainPlayer != null) {
            int key = e.getKeyCode();
            float moveAmount = 5.0f;

            switch (key) {
                case KeyEvent.VK_UP, KeyEvent.VK_W ->
                        mainPlayer.setPosition(new Player.Position(mainPlayer.getPosition().getX(), mainPlayer.getPosition().getY() - moveAmount));
                case KeyEvent.VK_DOWN, KeyEvent.VK_S ->
                        mainPlayer.setPosition(new Player.Position(mainPlayer.getPosition().getX(), mainPlayer.getPosition().getY() + moveAmount));
                case KeyEvent.VK_LEFT, KeyEvent.VK_A ->
                        mainPlayer.setPosition(new Player.Position(mainPlayer.getPosition().getX() - moveAmount, mainPlayer.getPosition().getY()));
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D ->
                        mainPlayer.setPosition(new Player.Position(mainPlayer.getPosition().getX() + moveAmount, mainPlayer.getPosition().getY()));
            }
            mainPlayer.ensurePlayerWithinBounds(gridSize);
            repaint();
        }
    }

    private void updatePlayerPositions() {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player player : cell.getPlayers()) {
                    if (player == mainPlayer) {
                        continue;
                    }
                    player.move(0.1f); // Move player with delta time (0.1 seconds for example)

                    // Ensure the player stays within bounds
                    if (player.getPosition().getX() < 0 || player.getPosition().getX() >= gridSize ||
                            player.getPosition().getY() < 0 || player.getPosition().getY() >= gridSize) {
                        player.setDirection(new Random().nextFloat() * 360); // Change direction randomly
                    }

                    // Update player's cell in the grid if necessary
                    int newCellX = (int) Math.floor(player.getPosition().getX() / cellSize);
                    int newCellY = (int) Math.floor(player.getPosition().getY() / cellSize);
                    int oldCellX = aoiSystem.getCellIndex(player.getPosition().getX() - player.getSpeed() * 0.1f * (float) Math.cos(Math.toRadians(player.getDirection())));
                    int oldCellY = aoiSystem.getCellIndex(player.getPosition().getY() - player.getSpeed() * 0.1f * (float) Math.sin(Math.toRadians(player.getDirection())));

                    if (newCellX != oldCellX || newCellY != oldCellY) {
                        cell.getPlayers().remove(player);
                        aoiSystem.addPlayer(player);
                    }

                    // Handle collision detection with other players
                    handlePlayerCollisions(player);
                }
            }
        }
        repaint(); // Redraw the grid with updated player positions
    }

    private void handlePlayerCollisions(Player player) {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player otherPlayer : cell.getPlayers()) {
                    if (player != otherPlayer && player.isCollision(otherPlayer)) {
                        // Perform action when collision occurs
//                        log.info("Collision detected between Player {} , and Player {}", player.getName(), otherPlayer.getName());
                        // Example action: Change player direction
                        player.setDirection(player.getDirection() + 180); // Reverse direction
//                        otherPlayer.setDirection(otherPlayer.getDirection() + 180); // Reverse direction
                        return; // Exit early to handle one collision at a time
                    }
                }
            }
        }
    }

    private void handleMouseClick(MouseEvent e) {
        Player player = Player.nextPlayer();
        player.getPosition().setX(e.getX());
        player.getPosition().setY(e.getY());
        aoiSystem.addPlayer(player);
        players.add(player);
        repaint(); // Redraw the grid with the new player and AOI
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawPlayers(g);
    }

    private void drawPlayers(Graphics g) {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player player : cell.getPlayers()) {
                    g.setColor(player.getColor());
                    int x = (int) player.getPosition().getX();
                    int y = (int) player.getPosition().getY();
                    g.fillOval(x - 5, y - 5, 10, 10); // Draw player as a small circle
                    g.drawOval(x - (int) player.getRadius(), y - (int) player.getRadius(),
                            (int) player.getRadius() * 2, (int) player.getRadius() * 2); // Draw player's radius
                    g.drawString(player.getName(), (int) (x - RADIUS), (int) (y - RADIUS));
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        int numCells = gridSize / cellSize;
        g.setColor(Color.BLACK);

        for (int i = 0; i <= numCells; i++) {
            int pos = i * cellSize;
            // Draw vertical lines
            g.drawLine(pos, 0, pos, gridSize);
            // Draw horizontal lines
            g.drawLine(0, pos, gridSize, pos);
        }
    }

    public void startGui() {
        JFrame frame = new JFrame("Grid Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add example players
        Player player1 = Player.nextPlayer();
        Player player2 = Player.nextPlayer();
        aoiSystem.addPlayer(player1);
        aoiSystem.addPlayer(player2);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }
}


