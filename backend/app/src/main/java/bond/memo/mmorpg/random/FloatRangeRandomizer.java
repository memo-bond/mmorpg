package bond.memo.mmorpg.random;

import org.jeasy.random.api.Randomizer;

public class FloatRangeRandomizer implements Randomizer<Float> {

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
