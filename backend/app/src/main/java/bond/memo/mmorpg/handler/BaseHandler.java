package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import com.google.protobuf.GeneratedMessageLite;
import io.netty.channel.ChannelHandlerContext;

import static bond.memo.mmorpg.utils.ByteUtils.protoMsgToBytes;

public abstract class BaseHandler <T extends GeneratedMessageLite> {

    protected T msg;
    protected final AOISystem aoiSystem;

    protected BaseHandler(AOISystem aoiSystem, T msg) {
        this.aoiSystem = aoiSystem;
        this.msg = msg;
    }

    protected void response(ChannelHandlerContext ctx) {
        PlayerActions.PlayerMessage responseMsg = PlayerActions.PlayerMessage.newBuilder()
                .setResponse(PlayerActions.Response.newBuilder().setSuccess(Boolean.TRUE).build())
                .build();
        ctx.writeAndFlush(protoMsgToBytes(responseMsg));
    }
}
