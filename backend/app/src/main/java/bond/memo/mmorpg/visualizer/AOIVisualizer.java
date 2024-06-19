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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static bond.memo.mmorpg.constants.Constants.RADIUS;

@Slf4j
public class AOIVisualizer extends JPanel {

    private final int gridSize;
    private final int cellSize;
    private final transient WebSocketClient client;
    private final transient JoinServerHandler joinServerHandler;
    private final transient PlayerMoveHandler playerMoveHandler;
    private final transient AOISystem aoiSystem;
    private final transient Player mainPlayer;
    private final transient List<Player> players;

    @Inject
    public AOIVisualizer(AOISystem aoiSystem, Player mainPlayer) {
        this.gridSize = aoiSystem.getGridSize();
        this.cellSize = aoiSystem.getCellSize();
        this.aoiSystem = aoiSystem;
        this.mainPlayer = mainPlayer;
        this.players = new CopyOnWriteArrayList<>(aoiSystem.getPlayerMap().values());
        this.client = WebSocketClient.of();
        this.joinServerHandler = new JoinServerHandler(this.client, new ConcurrentLinkedQueue<>());
        this.playerMoveHandler = new PlayerMoveHandler(this.client, new ConcurrentLinkedQueue<>());
        new Thread(joinServerHandler).start();
        new Thread(playerMoveHandler).start();

        aoiSystem.addPlayer(mainPlayer);
        joinServerHandler.addQueuePlayer(mainPlayer);

        setPreferredSize(new Dimension(gridSize, gridSize));
        Timer timer = new Timer(500, e -> {
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

    public void startGui() {
        JFrame frame = new JFrame("AOI Visualizer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Add example players
        Player p1 = PlayerService.nextPlayer();
        Player p2 = PlayerService.nextPlayer();
        log.info("Player 1 {}", p1);
        log.info("Player 2 {}", p2);
        aoiSystem.addPlayer(p1);
        aoiSystem.addPlayer(p2);

        joinServerHandler.addQueuePlayer(p1);
        joinServerHandler.addQueuePlayer(p2);

        frame.add(this);
        frame.pack();
        frame.setVisible(true);
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
            p.ensurePlayerWithinBounds(gridSize);
            repaint();
        }
    }

    private void move(Player p, float x, float y) {
        p.setPosition(Player.Position.from(x, y));
        playerMoveHandler.move(p.moveMsg());
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

    private void movePlayer(Player p) {
        p.moveGui(0.1f);

        if (p.isPlayerOutOfBounds(gridSize))
            p.setDirection(MyRandomizer.random().nextFloat() * 360);
        playerMoveHandler.move(p.moveMsg());
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
                    if (player.getId() != otherPlayer.getId() && player.isCollision(otherPlayer)) {
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
        player.position(e.getX(), e.getY());
        aoiSystem.addPlayer(player);
        players.add(player);
        joinServerHandler.addQueuePlayer(player);
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

    static class JoinServerHandler implements Runnable {

        private final WebSocketClient client;
        private final Queue<Player> queue;
        private volatile boolean running = true;

        public JoinServerHandler(WebSocketClient client, Queue<Player> queue) {
            this.client = client;
            this.queue = queue;
        }

        public void addQueuePlayer(Player player) {
            this.queue.add(player);
        }

        public void stop() {
            this.running = false;
        }

        @Override
        public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            while (running) {
                while (!queue.isEmpty() && client != null) {
                    Player p = queue.poll();
                    log.info("JoinServer player join {}", p);
                    if (p != null)
                        client.send(p.joinMsgBytes());
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            }
        }
    }

    static class PlayerMoveHandler implements Runnable {

        private final WebSocketClient client;
        private final Queue<PlayerActions.PlayerMessage> queue;
        private volatile boolean running = true;

        public PlayerMoveHandler(WebSocketClient client, Queue<PlayerActions.PlayerMessage> queue) {
            this.client = client;
            this.queue = queue;
        }

        public void move(PlayerActions.PlayerMessage move) {
            this.queue.add(move);
        }

        public void stop() {
            this.running = false;
        }

        @Override
        public void run() {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            while (running) {
                while (!queue.isEmpty() && client != null) {
                    PlayerActions.PlayerMessage move = queue.poll();
                    if (move != null)
                        client.send(move.toByteArray());
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(2));
                }
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            }
        }
    }
}


