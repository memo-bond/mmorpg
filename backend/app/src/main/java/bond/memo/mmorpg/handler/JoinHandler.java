package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;

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
            log.info("JOIN action player ID `{}`, name `{}`, x `{}`, y `{}`",
                    join.getId(), join.getName(), join.getX(), join.getY());

            Player mainPlayer = Player.builder()
                    .id(join.getId())
                    .name(join.getName())
                    .position(Player.Position.from(join.getX(), join.getY()))
                    .speed(1000)
                    .direction(200)
                    .color(Color.BLACK)
                    .main(Boolean.TRUE)
                    .build();

            aoiSystem.addPlayer(mainPlayer);

            response(ctx);
        }
    }
}
