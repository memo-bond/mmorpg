package bond.memo.mmorpg.visualizer;

import bond.memo.mmorpg.client.WebSocketClient;
import bond.memo.mmorpg.config.adapter.KeyListenerAdapter;
import bond.memo.mmorpg.config.adapter.MouseListenerAdapter;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.aoi.GridCell;
import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.random.MyRandomizer;
import bond.memo.mmorpg.service.PlayerService;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static bond.memo.mmorpg.constants.Constants.RADIUS;

@Slf4j
public class AOIVisualizer extends JPanel {

    private final int gridSize;
    private final int cellSize;
    private final WebSocketClient client;
    private final transient AOISystem aoiSystem;
    private final transient Player mainPlayer;
    private final transient List<Player> players;

    @Inject
    public AOIVisualizer(AOISystem aoiSystem, Player mainPlayer) {
        this.gridSize = aoiSystem.getGridSize();
        this.cellSize = aoiSystem.getCellSize();
        this.aoiSystem = aoiSystem;
        this.mainPlayer = mainPlayer;
        this.players = new CopyOnWriteArrayList<>();
        this.client = WebSocketClient.of();

        aoiSystem.addPlayer(mainPlayer);

        setPreferredSize(new Dimension(gridSize, gridSize));
        Timer timer = new Timer(100, e -> {
            updatePlayerPositions();
            checkMainPlayerCollisions();
            repaint();
        });
        timer.start();

        // Key listener for main player movement
        addKeyListener(KeyListenerAdapter.adapter(this::controlMainPlayer));
        addMouseListener(MouseListenerAdapter.adapter(this::handleMouseClick));
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
            Player p = mainPlayer;
            int key = e.getKeyCode();
            float moveAmount = 5.0f;

            switch (key) {
                case KeyEvent.VK_UP, KeyEvent.VK_W ->
                        move(p, p.getPosition().getX(), p.getPosition().getY() - moveAmount);
                case KeyEvent.VK_DOWN, KeyEvent.VK_S ->
                        move(p, p.getPosition().getX(), p.getPosition().getY() + moveAmount);
                case KeyEvent.VK_LEFT, KeyEvent.VK_A ->
                        move(p, p.getPosition().getX() - moveAmount, p.getPosition().getY());
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D ->
                        move(p, p.getPosition().getX() + moveAmount, p.getPosition().getY());
                default -> log.info("Unknown key code {}", key);
            }
            mainPlayer.ensurePlayerWithinBounds(gridSize);
            repaint();
        }
    }

    private void move(Player p, float x, float y) {
        p.setPosition(Player.Position.from(x, y));
        client.send(PlayerActions.PlayerMessage.newBuilder()
                .setMove(PlayerActions.Move.newBuilder()
                        .setId(p.getId()).setX(x).setY(y).build())
                .build().toByteArray()
        );
    }

    private void updatePlayerPositions() {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player player : cell.getPlayers()) {
                    if (player == mainPlayer || player.isMain()) {
                        continue;
                    }
                    movePlayer(player);
                    updatePlayerCell(player, cell);
                    handlePlayerCollisions(player);
                }
            }
        }
        repaint(); // Redraw the grid with updated player positions
    }

    private void movePlayer(Player player) {
        player.moveGui(0.1f);

        if (player.isPlayerOutOfBounds(gridSize))
            player.setDirection(MyRandomizer.random().nextFloat() * 360);
    }

    private void updatePlayerCell(Player player, GridCell cell) {
        int newCellX = (int) Math.floor(player.getPosition().getX() / cellSize);
        int newCellY = (int) Math.floor(player.getPosition().getY() / cellSize);
        int oldCellX = aoiSystem.getCellIndex(player.getPosition().getX() - player.getSpeed() * 0.1f * (float) Math.cos(Math.toRadians(player.getDirection())));
        int oldCellY = aoiSystem.getCellIndex(player.getPosition().getY() - player.getSpeed() * 0.1f * (float) Math.sin(Math.toRadians(player.getDirection())));

        if (newCellX != oldCellX || newCellY != oldCellY) {
            cell.getPlayers().remove(player);
            aoiSystem.addPlayer(player);
        }
    }

    private void handlePlayerCollisions(Player player) {
        for (Map<Integer, GridCell> column : aoiSystem.getGrid().values()) {
            for (GridCell cell : column.values()) {
                for (Player otherPlayer : cell.getPlayers()) {
                    if (player != otherPlayer && player.isCollision(otherPlayer)) {
                        // Perform action when collision occurs
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
        Player player = PlayerService.nextPlayer();
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
        JFrame frame = new JFrame("AOI Visualizer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add example players
        Player p1 = PlayerService.nextPlayer();
        Player p2 = PlayerService.nextPlayer();
        aoiSystem.addPlayer(p1);
        aoiSystem.addPlayer(p2);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }
}


