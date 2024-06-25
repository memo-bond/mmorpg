package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static bond.memo.mmorpg.utils.ByteUtils.protoMsgToBytes;

@Slf4j
public class MoveHandler extends BaseHandler<PlayerActions.Move> implements Handler {

    private MoveHandler(AOISystem aoiSystem, PlayerActions.Move msg) {
        super(aoiSystem, msg);
    }

    public static MoveHandler from(AOISystem aoiSystem, PlayerActions.Move move) {
        return new MoveHandler(aoiSystem, move);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Move move) {
            Player player = aoiSystem.getPlayerById(move.getId());
            if (player != null && player.isMain()) {
                log.info("MOVE action player `{}`", player);
            }
            Objects.requireNonNull(player, "player is null - check player join");
            player.move(move.getX(), move.getY());
            broadcastMove(player);
            response(ctx);
        }
    }

    private void broadcastMove(Player player) {
        List<Player> otherPlayers = aoiSystem.getPlayersInAOI(player);
        if (otherPlayers.size() == 1 && otherPlayers.getFirst().equals(player)) return;

        if (!otherPlayers.isEmpty() && !otherPlayers.contains(player)) {
            for (Player otherPlayer : otherPlayers) {
                log.info("broadcast move `{}`-`{}` to `{}`-`{}`",
                        player.getId(), player.getName(), otherPlayer.getId(), otherPlayer.getName());
            }
        }

        for (Player otherPlayer : otherPlayers) {
            if (otherPlayer.getId() != player.getId() && otherPlayer.getChannel() != null) {
                log.info("broadcast move {}, direction {}", player.moveMsg(), player.calcDirection());
                otherPlayer.getChannel().writeAndFlush(
                        protoMsgToBytes(player.moveMsg()))
                        .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("Failed to send move message to player ID {}, {}", otherPlayer.getId(), future.cause().getStackTrace());
                    }
                });
            }
        }
    }

}
