package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import com.google.protobuf.GeneratedMessageLite;

public class MoveHandler extends BaseHandler implements Handler {

    public MoveHandler(GeneratedMessageLite msg) {
        super(msg);
    }

    @Override
    public void handle() {
        if (msg instanceof PlayerActions.Move move) {
            System.out.println("MOVE action : " + move.getId() + " position x: " + move.getX() + " y: " + move.getY());
        }
    }
}
