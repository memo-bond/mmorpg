package bond.memo.mmorpg.module;

import bond.memo.mmorpg.GameServer;
import bond.memo.mmorpg.config.AppConfig;
import bond.memo.mmorpg.exception.LoadAppConfigException;
import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.service.AOISystem;
import bond.memo.mmorpg.service.PlayerService;
import bond.memo.mmorpg.service.aoi.AOISystemImpl;
import bond.memo.mmorpg.service.impl.PlayerServiceImpl;
import bond.memo.mmorpg.visualizer.AOIVisualizer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.awt.Color;
import java.io.InputStream;

import static bond.memo.mmorpg.constants.Constants.CELL_SIZE;
import static bond.memo.mmorpg.constants.Constants.GRID_SIZE;
import static bond.memo.mmorpg.constants.Constants.RADIUS;
import static bond.memo.mmorpg.converter.GridHeightConverter.unityToAoiY;

@Slf4j
public class GameModule extends AbstractModule {

    public static final String APP_CONFIG_PATH = "application_%s.yml";

    private String profile;

    public GameModule(String profile) {
        this.profile = profile;
    }

    public static GameModule from(String profile) {
        return new GameModule(profile);
    }

    @Override
    protected void configure() {
    }


    @Provides
    private AppConfig provideAppConfig() {
        Yaml yaml = new Yaml();
        log.info("Yaml loader is loading application config from `{}` profile to AppConfig", profile);
        String path = String.format(APP_CONFIG_PATH, profile);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            return yaml.loadAs(inputStream, AppConfig.class);
        } catch (Exception e) {
            throw new LoadAppConfigException("Failed to load configuration", e);
        }
    }

    @Provides
    @Singleton
    private GameServer provideGameServer(AOISystem aoiSystem, AppConfig config, PlayerService playerService) {
        return new GameServer(config.getServer().getPort(), aoiSystem, playerService);
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
                .id(123456).name("Louis").position(Player.Position.from(430, unityToAoiY(375)))
                .speed(200).radius(RADIUS).direction(200)
                .color(Color.RED)
                .build();
    }

    @Provides
    @Singleton
    private AOIVisualizer provideAOIVisualizer(AOISystem aoiSystem, Player mainPlayer, AppConfig config) {
        aoiSystem.addPlayer(mainPlayer);
        return new AOIVisualizer(aoiSystem, mainPlayer, config.serverHost());
    }

    @Provides
    @Singleton
    private PlayerService providePlayerService() {
        return new PlayerServiceImpl();
    }
}

