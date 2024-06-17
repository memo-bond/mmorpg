package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.exception.HandleBinaryDataException;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerHandler extends ChannelInboundHandlerAdapter {

    private final AOISystem aoiSystem;

    private PlayerHandler(AOISystem aoiSystem) {
        this.aoiSystem = aoiSystem;
    }

    public static PlayerHandler of(AOISystem aoiSystem) {
        return new PlayerHandler(aoiSystem);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (message instanceof WebSocketFrame) {
            switch (message) {
                case BinaryWebSocketFrame data -> handleBinaryData(ctx, data);
                case TextWebSocketFrame textWebSocketFrame -> {
                    log.info("TextWebSocketFrame Received : ");
                    ctx.channel().writeAndFlush(
                            new TextWebSocketFrame("Message recieved : " + textWebSocketFrame.text()));
                    log.info(textWebSocketFrame.text());
                }
                case PingWebSocketFrame pingWebSocketFrame -> {
                    log.info("PingWebSocketFrame Received : {}", pingWebSocketFrame.content());
                }
                case PongWebSocketFrame pongWebSocketFrame -> {
                    log.info("PongWebSocketFrame Received : {}", pongWebSocketFrame.content());
                }
                case CloseWebSocketFrame closeWebSocketFrame -> {
                    log.warn("CloseWebSocketFrame Received : ");
                    log.warn("ReasonText :{}", closeWebSocketFrame.reasonText());
                    log.warn("StatusCode : {}", closeWebSocketFrame.statusCode());
                }
                default -> log.error("Unsupported WebSocketFrame");
            }
        }
    }

    private void handleBinaryData(ChannelHandlerContext ctx, BinaryWebSocketFrame data) {
        try {
            PlayerActions.PlayerMessage msg = PlayerActions.PlayerMessage.parseFrom(ByteBufUtil.getBytes(data.content()));
            switch (msg.getActionCase()) {
                case JOIN -> JoinHandler.of(aoiSystem, msg.getJoin()).handle(ctx);
                case MOVE -> MoveHandler.of(aoiSystem, msg.getMove()).handle(ctx);
                case QUIT -> QuitHandler.of(aoiSystem, msg.getQuit()).handle(ctx);
                case ACTION_NOT_SET -> log.error("ACTION_NOT_SET");
                default -> log.error("Unknown action received: {}", msg.getActionCase());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new HandleBinaryDataException(e);
        }
    }
}
