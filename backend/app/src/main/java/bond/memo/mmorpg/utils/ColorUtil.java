package bond.memo.mmorpg.utils;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {

    private ColorUtil() {
    }

    private static final Random random = new Random();

    public static Color getRandomColor() {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }
}
