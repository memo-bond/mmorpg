package bond.memo.mmorpg.service;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.random.MyRandomizer;
import bond.memo.mmorpg.utils.ColorUtil;

public interface PlayerService extends Service {

    static Player nextPlayer() {
        Player player = MyRandomizer.nextObject(Player.class);
        player.setName(MyRandomizer.fullName());
        player.setColor(ColorUtil.getRandomColor());
        return player;
    }

    void handlePlayerDisconnect(Player player);
}
