package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.service.AOISystem;
import com.google.protobuf.GeneratedMessageLite;

public abstract class BaseHandler <T extends GeneratedMessageLite> {

    protected T msg;
    protected final AOISystem aoiSystem;

    protected BaseHandler(AOISystem aoiSystem, T msg) {
        this.aoiSystem = aoiSystem;
        this.msg = msg;
    }
}
