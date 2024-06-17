package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitHandler extends BaseHandler<PlayerActions.Quit> implements Handler {

    private QuitHandler(AOISystem aoiSystem, PlayerActions.Quit msg) {
        super(aoiSystem, msg);
    }

    public static QuitHandler of(AOISystem aoiSystem, PlayerActions.Quit move) {
        return new QuitHandler(aoiSystem, move);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Quit quit) {
            log.info("QUIT action player ID {}", quit.getId());
            aoiSystem.removePlayer(quit.getId());
            ctx.writeAndFlush(new TextWebSocketFrame("Player quit to position x: " + quit.getId()));
        }
    }
}
