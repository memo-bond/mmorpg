package bond.memo.mmorpg.handler;

import com.google.protobuf.GeneratedMessage;

public abstract class BaseHandler {
    protected GeneratedMessage msg;

    public BaseHandler(GeneratedMessage msg) {
        this.msg = msg;
    }
}
