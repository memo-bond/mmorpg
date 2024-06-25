package bond.memo.mmorpg.service.impl;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.service.PlayerService;

public class PlayerServiceImpl implements PlayerService {

    @Override
    public void handlePlayerDisconnect(Player player) {
        if (player.getChannel() != null) {
            player.getChannel().close();
            player.setChannel(null);
        }
    }
}
