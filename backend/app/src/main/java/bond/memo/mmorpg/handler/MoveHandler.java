package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.GeneratedMessageLite;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class MoveHandler extends BaseHandler implements Handler {

    public MoveHandler(GeneratedMessageLite msg) {
        super(msg);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Move move) {
            System.out.println("MOVE action : " + move.getId() + " position x: " + move.getX() + " y: " + move.getY());
            ctx.writeAndFlush(new TextWebSocketFrame("Player move to position x: " + move.getX() + " y: " + move.getY()));
        }
    }
}
