package bond.memo.mmorpg.handler;

import io.netty.channel.ChannelHandlerContext;

public interface Handler {
    void handle(ChannelHandlerContext ctx);
}
