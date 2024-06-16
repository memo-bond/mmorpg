package bond.memo.mmorpg.model;

import bond.memo.mmorpg.random.MyRandomizer;
import bond.memo.mmorpg.utils.ColorUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;
import java.util.Random;

@Slf4j
@Data
@Builder
public class Player {

    private int id;
    private String name;
    private Position position;
    private float direction;
    private float speed;
    private float radius;
    private Color color;
    private static final Random RANDOM = new Random();

    public static Player nextPlayer() {
        Player player = MyRandomizer.nextObject(Player.class);
        player.setName(MyRandomizer.fullName());
        player.setColor(ColorUtil.getRandomColor());
        return player;
    }

    public Player() {
    }

    public Player(int id, String name, Position position, float direction, float speed, float radius, Color color) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = direction;
        this.speed = speed;
        this.radius = radius;
        this.color = color == null ? ColorUtil.getRandomColor() : color;
    }

    public void move(float deltaTime) {
        float radians = (float) Math.toRadians(direction);
        position.setX(position.getX() + speed * (float) Math.cos(radians) * deltaTime);
        position.setY(position.getY() + speed * (float) Math.sin(radians) * deltaTime);
    }

    @Data
    public static class Position {
        private float x;
        private float y;

        public static Position of(float x, float y) {
            return new Position(x, y);
        }

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
