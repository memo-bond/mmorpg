package bond.memo.mmorpg.model;

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
    private boolean main;
    private static final Random RANDOM = new Random();

    public Player() {
    }

    public Player(int id, String name, Position position, float direction, float speed, float radius, Color color, boolean main) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = direction;
        this.speed = speed;
        this.radius = radius;
        this.color = color == null ? ColorUtil.getRandomColor() : color;
        this.main = main;
    }

    public void moveGui(float deltaTime) {
        float radians = (float) Math.toRadians(direction);
        position.setX(position.getX() + speed * (float) Math.cos(radians) * deltaTime);
        position.setY(position.getY() + speed * (float) Math.sin(radians) * deltaTime);
    }

    public void move(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public boolean isCollision(Player player2) {
        float dx = position.getX() - player2.getPosition().getX();
        float dy = position.getY() - player2.getPosition().getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= radius + player2.getRadius();
    }

    public void ensurePlayerWithinBounds(int gridSize) {
        float newX = Math.clamp(position.x, 0, gridSize);
        float newY = Math.clamp(position.y, 0, gridSize);
        this.setPosition(Player.Position.from(newX, newY));
    }

    public boolean isPlayerOutOfBounds(int gridSize) {
        return position.x < 0 || position.x >= gridSize ||
                position.y < 0 || position.y >= gridSize;
    }

    public double distance(Player.Position p2) {
        float dx = position.x - p2.getX();
        float dy = position.y - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Data
    public static class Position {
        private float x;
        private float y;

        public static Position from(float x, float y) {
            return new Position(x, y);
        }

        private Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
