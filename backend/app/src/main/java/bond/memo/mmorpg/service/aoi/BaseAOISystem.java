package bond.memo.mmorpg.service.aoi;

import bond.memo.mmorpg.service.AOISystem;

public abstract class BaseAOISystem implements AOISystem {

    public int getCellIndex(float coordinate) {
        return (int) Math.floor(coordinate / getCellSize());
    }

    abstract int getCellSize();
}
