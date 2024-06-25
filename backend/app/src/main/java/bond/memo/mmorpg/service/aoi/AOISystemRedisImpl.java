package bond.memo.mmorpg.service.aoi;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.repository.PlayerRepository;
import lombok.Getter;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bond.memo.mmorpg.constants.RedisConstants.UPDATE_PLAYER_POSITION_SCRIPT;

public class AOISystemRedisImpl extends BaseAOISystem {

    private static final String PLAYER_POSITION_KEY = "player:positions";
    private static final String GRID_CELL_KEY_PREFIX = "grid:cell:";
    private static final String CHANNEL_NAME = "player-movement";

    @Getter
    private final int gridSize;
    @Getter
    private final int cellSize;
    private final Jedis jedis;
    private final PlayerRepository playerRepository;

    public AOISystemRedisImpl(int gridSize, int cellSize, PlayerRepository playerRepository) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.playerRepository = playerRepository;
        this.jedis = new Jedis("localhost", 6379);
    }

    public void updatePlayerPosition(Player player, float newX, float newY) {
        String playerId = String.valueOf(player.getId());
        jedis.eval(UPDATE_PLAYER_POSITION_SCRIPT, 0, playerId, String.valueOf(newX), String.valueOf(newY), String.valueOf(cellSize));
        player.setPosition(Player.Position.from(newX, newY));
    }

    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }

    @Override
    public List<Player> getPlayers() {
        return List.of();
    }

    @Override
    public Map<Integer, Player> getPlayerMap() {
        return Map.of();
    }

    public void addPlayer(Player player) {
        updatePlayerPosition(player, player.getPosition().getX(), player.getPosition().getY());
    }

    @Override
    public Player getPlayerById(int playerId) {
        return null;
    }

    @Override
    public void removePlayer(int id) {

    }

    @Override
    public List<Player> getPlayersInAOI(Player player) {
        return null;
    }

    @Override
    public Map<Integer, Map<Integer, GridCell>> getGrid() {
        return Map.of();
    }
}
