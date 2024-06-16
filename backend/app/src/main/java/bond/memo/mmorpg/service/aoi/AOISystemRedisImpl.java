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
        player.setPosition(new Player.Position(newX, newY));
    }

    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void addPlayer(Player player) {
        updatePlayerPosition(player, player.getPosition().getX(), player.getPosition().getY());
    }

    @Override
    public List<Player> getPlayersInAOI(Player.Position position, float radius) {
        int minCellX = getCellIndex(position.getX() - radius);
        int maxCellX = getCellIndex(position.getX() + radius);
        int minCellY = getCellIndex(position.getY() - radius);
        int maxCellY = getCellIndex(position.getY() + radius);

        List<Player> playersInAOI = new ArrayList<>();
        for (int x = minCellX; x <= maxCellX; x++) {
            for (int y = minCellY; y <= maxCellY; y++) {
                String cellKey = GRID_CELL_KEY_PREFIX + x + ":" + y;
                List<String> playerIds = jedis.lrange(cellKey, 0, -1);
                for (String playerId : playerIds) {
                    Player player = playerRepository.getPlayer(Integer.parseInt(playerId));
                    if (player != null) {
                        playersInAOI.add(player);
                    }
                }
            }
        }
        return playersInAOI;
    }

    @Override
    public Map<Integer, Map<Integer, GridCell>> getGrid() {
        return Map.of();
    }
}
