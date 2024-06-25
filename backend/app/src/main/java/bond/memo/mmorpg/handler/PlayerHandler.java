package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.exception.HandleBinaryDataException;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.PlayerService;
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
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayerHandler extends ChannelInboundHandlerAdapter {

    private final AOISystem aoiSystem;
    private final PlayerService playerService;

    private PlayerHandler(AOISystem aoiSystem, PlayerService playerService) {
        this.aoiSystem = aoiSystem;
        this.playerService = playerService;
    }

    public static PlayerHandler from(AOISystem aoiSystem, PlayerService playerService) {
        return new PlayerHandler(aoiSystem, playerService);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        try {
            if (message instanceof WebSocketFrame) {
                switch (message) {
                    case BinaryWebSocketFrame data -> handleBinaryData(ctx, data);
                    case TextWebSocketFrame text -> handleTextData(ctx, text);
                    case PingWebSocketFrame ping -> log.info("PingWebSocketFrame Received : {}", ping.content());
                    case PongWebSocketFrame pong -> log.info("PongWebSocketFrame Received : {}", pong.content());
                    case CloseWebSocketFrame signal -> handleCloseEvent(signal);
                    default -> log.error("Unsupported WebSocketFrame");
                }
            }
        } finally {
            ReferenceCountUtil.release(message); // Ensure that the buffer is released
        }
    }

    private static void handleCloseEvent(CloseWebSocketFrame closeWebSocketFrame) {
        log.warn("CloseWebSocketFrame Received : ");
        log.warn("ReasonText :{}", closeWebSocketFrame.reasonText());
        log.warn("StatusCode : {}", closeWebSocketFrame.statusCode());
    }

    private static void handleTextData(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
        log.info("TextWebSocketFrame Received : ");
        ctx.channel().writeAndFlush(
                new TextWebSocketFrame("Message recieved : " + textWebSocketFrame.text()));
        log.info(textWebSocketFrame.text());
    }

    private void handleBinaryData(ChannelHandlerContext ctx, BinaryWebSocketFrame data) {
        try {
            PlayerActions.PlayerMessage msg = PlayerActions.PlayerMessage.parseFrom(ByteBufUtil.getBytes(data.content()));
            switch (msg.getActionCase()) {
                case JOIN -> JoinHandler.from(aoiSystem, msg.getJoin()).handle(ctx);
                case MOVE -> MoveHandler.from(aoiSystem, msg.getMove()).handle(ctx);
                case QUIT -> QuitHandler.from(aoiSystem, playerService, msg.getQuit()).handle(ctx);
                case LEAVE -> LeaveHandler.from(aoiSystem, msg.getLeave()).handle(ctx);
                case ACTION_NOT_SET -> log.error("ACTION_NOT_SET");
                default -> log.error("Unknown action received: {}", msg.getActionCase());
            }
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new HandleBinaryDataException(e);
        }
    }
}
