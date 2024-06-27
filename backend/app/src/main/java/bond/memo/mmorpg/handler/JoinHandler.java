package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.PlayerService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;

import static bond.memo.mmorpg.constants.Constants.RADIUS;

@Slf4j
public class JoinHandler extends BaseHandler<PlayerActions.Join> implements Handler {

    private JoinHandler(AOISystem aoiSystem, PlayerActions.Join msg) {
        super(aoiSystem, msg);
    }

    public static JoinHandler from(AOISystem aoiSystem, PlayerActions.Join msg) {
        return new JoinHandler(aoiSystem, msg);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Join join) {
            Player player = PlayerService.nextPlayer();
            player.setId(join.getId());
            player.setName(join.getName());
            player.setPosition(Player.Position.from(join.getX(), join.getY()));
            player.setChannel(ctx.channel());
            if (join.getUnity()) {
                player.setUnity(Boolean.TRUE);
                player.setMain(Boolean.TRUE);
            }
            // temp condition for main player instead of bot
            if (join.getId() == 123456) // louis
                player.setMain(Boolean.TRUE);
            log.info("Player join server `{}`", player);
            aoiSystem.addPlayer(player);
            response(ctx);
        }
    }
}
