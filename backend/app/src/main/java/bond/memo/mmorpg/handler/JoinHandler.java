package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;

@Slf4j
public class JoinHandler extends BaseHandler<PlayerActions.Join> implements Handler {

    private JoinHandler(AOISystem aoiSystem, PlayerActions.Join msg) {
        super(aoiSystem, msg);
    }

    public static JoinHandler of(AOISystem aoiSystem, PlayerActions.Join msg) {
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
                    .position(Player.Position.of(join.getX() + 100, join.getY() + 100))
                    .speed(100)
                    .direction(200)
                    .color(Color.BLACK)
                    .main(Boolean.TRUE)
                    .build();

            aoiSystem.addPlayer(mainPlayer);

            ctx.writeAndFlush(new TextWebSocketFrame("Welcome to MMORPG"));
        }
    }
}
