package bond.memo.mmorpg.random;

import bond.memo.mmorpg.model.Player;
import com.github.javafaker.Faker;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.awt.Color;
import java.util.Random;

import static org.jeasy.random.FieldPredicates.inClass;
import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class MyRandomizer {

    private static final MyRandomizer MY_RANDOMIZER = new MyRandomizer();
    private final EasyRandom easyRandom;
    private final Faker faker;
    private final Random random;

    private MyRandomizer() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .randomize(field -> field.getName().equals("speed"), new FloatRangeRandomizer(50.0f, 100.0f))
                .randomize(field -> field.getName().equals("radius"), new FloatRangeRandomizer(40.0f, 60.0f))
                .randomize(field -> field.getName().equals("x"), new FloatRangeRandomizer(0.0f, 900.0f))
                .randomize(field -> field.getName().equals("y"), new FloatRangeRandomizer(0.0f, 900.0f))
                .randomize(field -> field.getName().equals("direction"), new FloatRangeRandomizer(0.0f, 360.0f))
                .excludeField(named("radius").and(ofType(Float.class)).and(inClass(Player.class)))
                .excludeField(named("color").and(ofType(Color.class)).and(inClass(Player.class)));
        easyRandom = new EasyRandom(parameters);
        this.faker = new Faker();
        this.random = new Random();
    }

    public static <T> T nextObject(final Class<T> type) {
        return MY_RANDOMIZER.easyRandom.nextObject(type);
    }

    public static String fullName() {
        return MY_RANDOMIZER.faker.name().fullName();
    }

    public static Random random() {
        return MY_RANDOMIZER.random;
    }
}
