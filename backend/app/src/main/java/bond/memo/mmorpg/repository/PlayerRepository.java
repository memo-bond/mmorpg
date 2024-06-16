package bond.memo.mmorpg.repository;

import bond.memo.mmorpg.model.Player;

public interface PlayerRepository extends Repository {

    void savePlayer(Player player);

    Player getPlayer(int id);
}
