package bond.memo.mmorpg.handler;

import com.google.protobuf.GeneratedMessageLite;

public abstract class BaseHandler {
    protected GeneratedMessageLite msg;

    public BaseHandler(GeneratedMessageLite msg) {
        this.msg = msg;
    }
}
