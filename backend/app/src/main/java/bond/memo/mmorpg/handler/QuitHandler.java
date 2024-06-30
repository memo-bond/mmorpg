package bond.memo.mmorpg.handler;

import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.PlayerService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuitHandler extends BaseHandler<PlayerActions.Quit> implements Handler {

    private final PlayerService playerService;

    private QuitHandler(AOISystem aoiSystem, PlayerService playerService, PlayerActions.Quit msg) {
        super(aoiSystem, msg);
        this.playerService = playerService;
    }

    public static QuitHandler from(AOISystem aoiSystem, PlayerService playerService, PlayerActions.Quit move) {
        return new QuitHandler(aoiSystem, playerService, move);
    }

    @Override
    public void handle(ChannelHandlerContext ctx) {
        if (msg instanceof PlayerActions.Quit quit) {
            log.info("QUIT action player ID {}", quit.getId());
            playerService.handlePlayerDisconnect(aoiSystem.getPlayerById(quit.getId()));
            aoiSystem.removePlayer(quit.getId());
            response(ctx);
        }
    }
}
