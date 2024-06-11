package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.GeneratedMessage;

public class JoinHandler extends BaseHandler implements Handler {

    public JoinHandler(GeneratedMessage msg) {
        super(msg);
    }

    @Override
    public void handle() {
        if (msg instanceof PlayerActions.Join join) {
            System.out.println("JOIN action : " + join.getId() + " : " + join.getName() + " - XXX x: " + join.getX() + " y: " + join.getY());
        }
    }
}
