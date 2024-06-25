package bond.memo.mmorpg.service;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.service.aoi.GridCell;

import java.util.List;
import java.util.Map;

public interface AOISystem {

    List<Player> getPlayers();

    Map<Integer, Player> getPlayerMap();

    void addPlayer(Player player);

    Player getPlayerById(int playerId);

    void removePlayer(int id);

    List<Player> getPlayersInAOI(Player player);

    Map<Integer, Map<Integer, GridCell>> getGrid();

    int getCellIndex(float v);

    int getGridSize();

    int getCellSize();
}
