package bond.memo.mmorpg.aoi;

import bond.memo.mmorpg.model.Player;

import java.util.List;
import java.util.Map;

public interface AOISystem {

    void addPlayer(Player player);

    List<Player> getPlayersInAOI(Player.Position position, float radius);

    Map<Integer, Map<Integer, GridCell>> getGrid();
}