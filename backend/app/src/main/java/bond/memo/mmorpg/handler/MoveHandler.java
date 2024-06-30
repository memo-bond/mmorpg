package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

import static bond.memo.mmorpg.converter.GridHeightConverter.aoiToUnityY;
import static bond.memo.mmorpg.converter.GridHeightConverter.unityToAoiY;
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
            if (player == null) {
                log.warn("This player `{}` is not join & initial properly - so it is null", move.getId());
                return;
            }
            player.move(move.getX(),
                    player.isUnity() ? unityToAoiY(move.getY()) : move.getY(),
                    move.getDirection().ordinal());
            aoiSystem.movePlayer(player);
            broadcastMove(player);
            response(ctx);
        }
    }

    private void broadcastMove(Player player) {
        Set<Player> otherPlayers = aoiSystem.getPlayersInAOI(player);
        if (otherPlayers.size() == 1) return;

        if (!otherPlayers.isEmpty() && !otherPlayers.contains(player)) {
            for (Player otherPlayer : otherPlayers) {
                if (otherPlayer.getId() == 123456)
                    log.info("broadcast move `{}`-`{}` to `{}`-`{}`",
                            player.getId(), player.getName(), otherPlayer.getId(), otherPlayer.getName());
            }
        }

        for (Player otherPlayer : otherPlayers) {
            if (otherPlayer.getId() == player.getId()) continue; // exclude current player
            if (otherPlayer.getChannel() != null) {
                log.info("broadcast move {}", player.moveMsg(aoiToUnityY(player.getPosition().getY())));
                try {
                    otherPlayer.getChannel()
                            .writeAndFlush(protoMsgToBytes(player.moveMsg(
                                    otherPlayer.isUnity()
                                            ? aoiToUnityY(player.getPosition().getY())
                                            : player.getPosition().getY()
                            )))
                            .addListener((ChannelFutureListener) future -> {
                                if (!future.isSuccess()) {
                                    log.error("Failed to send move message to player ID {}, {}", otherPlayer.getId(), future.cause().getStackTrace());
                                }
                            });
                } catch (Exception e) {
                    log.error("broadcast move failed", e);
                }

            }
        }
    }

}
