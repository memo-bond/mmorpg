package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (message instanceof WebSocketFrame) {
            switch (message) {
                case BinaryWebSocketFrame data -> handleBinaryData(data);
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

    private static void handleBinaryData(BinaryWebSocketFrame data) {
        try {
            PlayerActions.PlayerMessage msg = PlayerActions.PlayerMessage
                    .parseFrom(ByteBufUtil.getBytes(data.content()));
            switch (msg.getActionCase()) {
                case JOIN:
                    new JoinHandler(msg.getJoin()).handle();
                    break;
                case MOVE:
                    new MoveHandler(msg.getMove()).handle();
                    break;
                case ACTION_NOT_SET:
                    log.error("ACTION_NOT_SET");
                    break;
                default:
                    log.error("Unknown action received: {}", msg.getActionCase());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("InvalidProtocolBufferException : ", e);
        }
    }
}
