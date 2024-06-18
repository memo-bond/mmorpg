package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeaveHandler extends BaseHandler<PlayerActions.Leave> implements Handler {

    private LeaveHandler(AOISystem aoiSystem, PlayerActions.Leave msg) {
        super(aoiSystem, msg);
    }

    public static LeaveHandler of(AOISystem aoiSystem, PlayerActions.Leave leave) {
        return new LeaveHandler(aoiSystem, leave);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Leave leave) {
            log.info("QUIT action player ID {}", leave.getId());
            aoiSystem.removePlayer(leave.getId());

            response(ctx);
        }
    }
}
