package bond.memo.mmorpg.module;

import bond.memo.mmorpg.GameServer;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.aoi.AOISystemImpl;
import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.visualizer.AOIVisualizer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.awt.Color;

import static bond.memo.mmorpg.constants.Constants.CELL_SIZE;
import static bond.memo.mmorpg.constants.Constants.GRID_SIZE;
import static bond.memo.mmorpg.constants.Constants.RADIUS;
import static bond.memo.mmorpg.constants.Constants.SERVER_PORT;

public class GameModule extends AbstractModule {

    private static final GameModule INSTANCE = new GameModule();

    public static GameModule of() {
        return INSTANCE;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    private GameServer provideGameServer(AOISystem aoiSystem) {
        return new GameServer(SERVER_PORT, aoiSystem);
    }

    @Provides
    @Singleton
    private AOISystem provideAOISystem() {
        return new AOISystemImpl(GRID_SIZE, CELL_SIZE);
    }

    @Provides
    @Singleton
    private Player provideMainPlayer() {
        return Player.builder()
                .id(123456).name("Louis").position(Player.Position.from(200, 300))
                .speed(200).radius(RADIUS).direction(200)
                .color(Color.RED)
                .build();
    }

    @Provides
    @Singleton
    private AOIVisualizer provideAOIVisualizer(AOISystem aoiSystem, Player mainPlayer) {
        aoiSystem.addPlayer(mainPlayer);
        return new AOIVisualizer(aoiSystem, mainPlayer);
    }
}

