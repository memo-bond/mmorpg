package bond.memo.mmorpg.aoi.visual;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GridVisualizer extends JPanel {

    private static final float radius = 50;
    private final int gridSize;
    private final int cellSize;
    private Player.Position selectedPosition;
    private final AOISystem aoiSystem;
    private final AtomicInteger playerIdCounter = new AtomicInteger(3);
    private Timer timer;

    public GridVisualizer(int gridSize, int cellSize, AOISystem aoiSystem) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.aoiSystem = aoiSystem;
        setPreferredSize(new Dimension(gridSize, gridSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
        timer = new Timer(100, e -> updatePlayerPositions());
        timer.start();
    }

    private void updatePlayerPositions() {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player player : cell.getPlayers()) {
                    player.move(0.1f); // Move player with delta time (0.1 seconds for example)

                    // Ensure the player stays within bounds
                    if (player.getPosition().getX() < 0 || player.getPosition().getX() >= gridSize ||
                            player.getPosition().getY() < 0 || player.getPosition().getY() >= gridSize) {
                        player.setDirection(new Random().nextFloat() * 360); // Change direction randomly
                    }

                    // Update player's cell in the grid if necessary
                    int newCellX = (int) Math.floor(player.getPosition().getX() / cellSize);
                    int newCellY = (int) Math.floor(player.getPosition().getY() / cellSize);
                    int oldCellX = getCellIndex(player.getPosition().getX() - player.getSpeed() * 0.1f * (float) Math.cos(Math.toRadians(player.getDirection())));
                    int oldCellY = getCellIndex(player.getPosition().getY() - player.getSpeed() * 0.1f * (float) Math.sin(Math.toRadians(player.getDirection())));

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
                    if (player != otherPlayer && isCollision(player, otherPlayer)) {
                        // Perform action when collision occurs
                        log.info("Collision detected between Player {} , and Player {}", player.getName(), otherPlayer.getName());
                        // Example action: Change player direction
                        player.setDirection(player.getDirection() + 180); // Reverse direction
//                        otherPlayer.setDirection(otherPlayer.getDirection() + 180); // Reverse direction
                        return; // Exit early to handle one collision at a time
                    }
                }
            }
        }
    }

    private boolean isCollision(Player player1, Player player2) {
        float dx = player1.getPosition().getX() - player2.getPosition().getX();
        float dy = player1.getPosition().getY() - player2.getPosition().getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= player1.getRadius() + player2.getRadius();
    }

    private int getCellIndex(float coordinate) {
        return (int) Math.floor(coordinate / cellSize);
    }

    private void handleMouseClick(MouseEvent e) {
        Player player = Player.nextPlayer();
        aoiSystem.addPlayer(player);
        player.getPosition().setX(e.getX());
        player.getPosition().setY(e.getY());
        selectedPosition = new Player.Position(player.getPosition().getX(), player.getPosition().getY());
        repaint(); // Redraw the grid with the new player and AOI
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawPlayers(g);
        if (selectedPosition != null) {
            drawAOI(g, selectedPosition);
        }
    }

    private void drawAOI(Graphics g, Player.Position position) {
        g.setColor(Color.BLUE);
        List<Player> playersInAOI = aoiSystem.getPlayersInAOI(position, radius);
        for (Player player : playersInAOI) {
            int x = (int) player.getPosition().getX();
            int y = (int) player.getPosition().getY();
            g.fillOval(x - 5, y - 5, 10, 10); // Highlight player in AOI as a blue circle
        }

        int x = (int) position.getX();
        int y = (int) position.getY();
        int r = (int) radius;
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
    }

    private void drawPlayers(Graphics g) {
        g.setColor(Color.RED);
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player player : cell.getPlayers()) {
                    int x = (int) player.getPosition().getX();
                    int y = (int) player.getPosition().getY();
                    g.fillOval(x - 5, y - 5, 10, 10); // Draw player as a small circle
                    g.drawOval(x - (int) player.getRadius(), y - (int) player.getRadius(),
                            (int) player.getRadius() * 2, (int) player.getRadius() * 2); // Draw player's radius
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Grid Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int gridSize = 1000; // Total size of the grid
        int cellSize = 100;  // Size of each cell

        int numCells = gridSize / cellSize;
        int maxX = (numCells - 1) * cellSize;
        int maxY = (numCells - 1) * cellSize;

        System.out.println("Max X: " + maxX);
        System.out.println("Max Y: " + maxY);

        AOISystem aoiSystem = new AOISystemImpl(gridSize, cellSize);

        // Add example players
        Player player1 = Player.nextPlayer();
        Player player2 = Player.nextPlayer();
        aoiSystem.addPlayer(player1);
        aoiSystem.addPlayer(player2);

        GridVisualizer gridVisualizer = new GridVisualizer(gridSize, cellSize, aoiSystem);
        frame.add(gridVisualizer);
        frame.pack();
        frame.setVisible(true);
    }
}


