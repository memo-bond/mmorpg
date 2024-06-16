package bond.memo.mmorpg.repository.impl;

import bond.memo.mmorpg.model.Player;
import bond.memo.mmorpg.repository.PlayerRepository;
import redis.clients.jedis.Jedis;

import java.awt.Color;

public class PlayerRepositoryRedisImpl implements PlayerRepository {

    private final Jedis jedis;

    public PlayerRepositoryRedisImpl() {
        this.jedis = new Jedis("localhost", 6379);
    }

    public void savePlayer(Player player) {
        // Store player attributes in Redis hash
        String playerId = String.valueOf(player.getId());
        jedis.hset("player:" + playerId, "name", player.getName());
        jedis.hset("player:" + playerId, "position_x", String.valueOf(player.getPosition().getX()));
        jedis.hset("player:" + playerId, "position_y", String.valueOf(player.getPosition().getY()));
        jedis.hset("player:" + playerId, "direction", String.valueOf(player.getDirection()));
        jedis.hset("player:" + playerId, "speed", String.valueOf(player.getSpeed()));
        jedis.hset("player:" + playerId, "radius", String.valueOf(player.getRadius()));
        jedis.hset("player:" + playerId, "color", String.valueOf(player.getColor().getRGB()));
    }

    public Player getPlayer(int id) {
        String playerId = String.valueOf(id);
        String name = jedis.hget("player:" + playerId, "name");
        float positionX = Float.parseFloat(jedis.hget("player:" + playerId, "position_x"));
        float positionY = Float.parseFloat(jedis.hget("player:" + playerId, "position_y"));
        float direction = Float.parseFloat(jedis.hget("player:" + playerId, "direction"));
        float speed = Float.parseFloat(jedis.hget("player:" + playerId, "speed"));
        float radius = Float.parseFloat(jedis.hget("player:" + playerId, "radius"));
        int colorRGB = Integer.parseInt(jedis.hget("player:" + playerId, "color"));
        Color color = new Color(colorRGB);

        return new Player(id, name, new Player.Position(positionX, positionY), speed, radius, direction, color);
    }

    public void close() {
        jedis.close();
    }
}

