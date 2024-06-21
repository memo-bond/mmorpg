package bond.memo.mmorpg.service.aoi;


import bond.memo.mmorpg.model.Player;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AOISystemImpl extends BaseAOISystem {

    @Getter
    private final int gridSize;
    @Getter
    private final int cellSize;
    @Getter
    private final Map<Integer, Map<Integer, GridCell>> grid = new ConcurrentHashMap<>();
    @Getter
    private final Map<Integer, Player> playerMap = new ConcurrentHashMap<>();

    @Override
    public List<Player> getPlayers() {
        return new CopyOnWriteArrayList<>(playerMap.values());
    }

    public AOISystemImpl(int gridSize, int cellSize) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
    }

    public void addPlayer(Player player) {
        int cellX = getCellIndex(player.getPosition().getX());
        int cellY = getCellIndex(player.getPosition().getY());
        grid.computeIfAbsent(cellX, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(cellY, k -> new GridCell())
                .getPlayers().add(player);
        playerMap.put(player.getId(), player);
    }

    @Override
    public Player getPlayerById(int playerId) {
        return playerMap.get(playerId);
    }

    @Override
    public void removePlayer(int id) {
        Player player = getPlayerById(id);
        int cellX = getCellIndex(player.getPosition().getX());
        int cellY = getCellIndex(player.getPosition().getY());
        Map<Integer, GridCell> column = grid.get(cellX);
        if (column != null) {
            GridCell cell = column.get(cellY);
            if (cell != null) {
                cell.getPlayers().remove(player);
                if (cell.getPlayers().isEmpty()) {
                    column.remove(cellY);
                }
            }
            if (column.isEmpty()) {
                grid.remove(cellX);
            }
        }
        playerMap.remove(player.getId()); // Remove player from the ID map
    }

    public List<Player> getPlayersInAOI(Player.Position position, float radius) {
        int minCellX = getCellIndex(position.getX() - radius);
        int maxCellX = getCellIndex(position.getX() + radius);
        int minCellY = getCellIndex(position.getY() - radius);
        int maxCellY = getCellIndex(position.getY() + radius);

        List<Player> entitiesInAOI = new LinkedList<>();
        for (int x = minCellX; x <= maxCellX; x++) {
            for (int y = minCellY; y <= maxCellY; y++) {
                Map<Integer, GridCell> column = grid.get(x);
                if (column != null) {
                    GridCell cell = column.get(y);
                    if (cell != null) {
                        entitiesInAOI.addAll(cell.getPlayers());
                    }
                }
            }
        }
        // Filter players within the exact radius
        List<Player> playersWithinRadius = new LinkedList<>();
        for (Player player : entitiesInAOI) {
            if (distance(position, player.getPosition()) <= radius) {
                playersWithinRadius.add(player);
            }
        }

        return playersWithinRadius;
    }

    private double distance(Player.Position p1, Player.Position p2) {
        float dx = p1.getX() - p2.getX();
        float dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
