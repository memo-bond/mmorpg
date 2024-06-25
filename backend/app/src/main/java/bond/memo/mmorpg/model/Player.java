package bond.memo.mmorpg.model;

import bond.memo.mmorpg.enums.MoveDirection;
import bond.memo.mmorpg.models.PlayerActions;
import bond.memo.mmorpg.utils.ColorUtil;
import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.Color;

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
    private Channel channel;
    private MoveDirection moveDirection;

    public Player() {
    }

    public Player(int id, String name, Position position, float direction,
                  float speed, float radius, Color color, boolean main, Channel channel, MoveDirection moveDirection) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = direction;
        this.speed = speed;
        this.radius = radius;
        this.color = color == null ? ColorUtil.getRandomColor() : color;
        this.main = main;
        this.channel = channel;
        this.moveDirection = MoveDirection.DOWN;
    }

    public PlayerActions.PlayerMessage moveMsg() {
        var move = PlayerActions.Move.newBuilder()
                .setId(id).setName(name).setX(position.x).setY(position.y)
                .setDirection(PlayerActions.MoveDirection.forNumber(calcDirection().ordinal()))
                .build();
        return PlayerActions.PlayerMessage.newBuilder()
                .setMove(move)
                .build();
    }

    public MoveDirection calcDirection() {
        direction = direction % 360;
        if (direction < 0) {
            direction += 360;
        }
        // Determine the movement direction
        if (direction >= 45 && direction < 135) {
            return MoveDirection.UP;
        } else if (direction >= 135 && direction < 225) {
            return MoveDirection.LEFT;
        } else if (direction >= 225 && direction < 315) {
            return MoveDirection.DOWN;
        } else {
            return MoveDirection.RIGHT;
        }
    }

    public byte[] joinMsgBytes() {
        return PlayerActions.PlayerMessage.newBuilder()
                .setJoin(PlayerActions.Join.newBuilder()
                        .setId(id).setName(name).setX(position.x).setY(position.y).build())
                .build().toByteArray();
    }

    public void moveGui(float deltaTime) {
        float radians = (float) Math.toRadians(direction);
        position.setX(position.getX() + speed * (float) Math.cos(radians) * deltaTime);
        position.setY(position.getY() + speed * (float) Math.sin(radians) * deltaTime);
    }

    public void move(float x, float y, int direction) {
        position.x = x;
        position.y = y;
        this.direction = MoveDirection.values()[direction].degree();
    }

    public boolean isMain() {
        return id > 0 && id < 100 // temp condition for condition from Unity instead of GUI
                && main;
    }

    public boolean isCollision(Player otherPlayer) {
        if (id == otherPlayer.getId()) return false;
        float dx = position.getX() - otherPlayer.getPosition().getX();
        float dy = position.getY() - otherPlayer.getPosition().getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        boolean collided = distance <= radius + otherPlayer.getRadius();
//        if (collided)
//            log.info("Player ID `{}` name `{}` collided with player ID `{}` name `{}`", id, name, otherPlayer.getId(), otherPlayer.getName());
        return collided;
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

    public void position(int x, int y) {
        position.x = x;
        position.y = y;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
