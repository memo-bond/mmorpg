package bond.memo.mmorpg.config;

import lombok.Data;

@Data
public class AppConfig {

    private ServerConfig server;
    private DataSourceConfig datasource;
    private Game game;

    public int serverPort() {
        return server.getPort();
    }

    public String dbUrl() {
        return datasource.getUrl();
    }

    public String dbUsername() {
        return datasource.getUsername();
    }

    public String dbPassword() {
        return datasource.getPassword();
    }

    public int cellSize() {
        return game.getConfig().getCellSize();
    }

    public int gridSize() {
        return game.getConfig().getGridSize();
    }

    public float radius() {
        return game.getConfig().getRadius();
    }

    public boolean showUI() {
        return game.getConfig().isShowUI();
    }

    public String serverHost() {
        return game.getConfig().getServerHost();
    }

    @Data
    public static class ServerConfig {
        private int port;
    }
    @Data
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
    }
    @Data
    public static class Game {
        private Config config;
        @Data
        public static class Config {
            private int cellSize;
            private int gridSize;
            private float radius;
            private boolean showUI;
            private String serverHost;
        }
    }
}
