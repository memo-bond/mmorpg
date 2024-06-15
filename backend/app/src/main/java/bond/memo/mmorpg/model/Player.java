package bond.memo.mmorpg.model;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.api.Randomizer;

import java.util.Random;

import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

@Slf4j
@Data
public class Player {
    private int id;
    private String name;
    private Position position;
    private float direction;
    private float speed;
    private final float radius;
    private static final Random RANDOM = new Random();

    private static EasyRandomParameters parameters = new EasyRandomParameters()
            .randomize(field -> field.getName().equals("speed"), new FloatRangeRandomizer(50.0f, 100.0f))
            .randomize(field -> field.getName().equals("radius"), new FloatRangeRandomizer(40.0f, 60.0f))
            .randomize(field -> field.getName().equals("x"), new FloatRangeRandomizer(0.0f, 900.0f))
            .randomize(field -> field.getName().equals("y"), new FloatRangeRandomizer(0.0f, 900.0f))
            .randomize(field -> field.getName().equals("direction"), new FloatRangeRandomizer(0.0f, 360.0f))
            .excludeField(named("radius").and(ofType(Float.class)).and(inClass(Player.class)));
    private static EasyRandom easyRandom = new EasyRandom(parameters);
    private static Faker faker = new Faker();;

    public static Player nextPlayer() {
        Player player = easyRandom.nextObject(Player.class);
        player.setName(faker.name().fullName());
        return player;
    }

    static class FloatRangeRandomizer implements Randomizer<Float> {

        private final float min;
        private final float max;

        public FloatRangeRandomizer(float min, float max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public Float getRandomValue() {
            return min + (float) Math.random() * (max - min);
        }
    }


    public Player(int id, String name, Position position, float speed, float radius) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.speed = 100 + RANDOM.nextInt(200);
        this.direction = new Random().nextFloat() * 360; // Random initial direction
        this.radius = radius;
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

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
