package bond.memo.mmorpg.aoi;

public abstract class BaseAOISystem implements AOISystem {

    public int getCellIndex(float coordinate) {
        return (int) Math.floor(coordinate / getCellSize());
    }

    abstract int getCellSize();
}
