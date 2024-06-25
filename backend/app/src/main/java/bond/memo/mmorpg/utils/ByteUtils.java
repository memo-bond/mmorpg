package bond.memo.mmorpg.utils;

import com.google.protobuf.GeneratedMessageLite;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public final class ByteUtils {
    private ByteUtils() {}

    public static BinaryWebSocketFrame protoMsgToBytes(GeneratedMessageLite msg) {
        return new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.toByteArray()));
    }
}
