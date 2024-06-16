package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.GeneratedMessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JoinHandler extends BaseHandler implements Handler {

    public JoinHandler(GeneratedMessageLite msg) {
        super(msg);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Join join) {
            log.info("JOIN action player ID `{}`, name `{}`, x `{}`, y `{}`",
                    join.getId(), join.getName(), join.getX(), join.getY());
            ctx.writeAndFlush(new TextWebSocketFrame("Welcome to MMORPG"));
        }
    }
}
