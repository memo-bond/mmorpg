package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.GeneratedMessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class JoinHandler extends BaseHandler implements Handler {

    public JoinHandler(GeneratedMessageLite msg) {
        super(msg);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Join join) {
            System.out.println("JOIN action : " + join.getId() + " : " + join.getName() + " - XXX x: " + join.getX() + " y: " + join.getY());
            ctx.writeAndFlush(new TextWebSocketFrame("Welcome to MMOrpg"));
        }
    }
}
