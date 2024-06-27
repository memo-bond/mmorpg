package bond.memo.mmorpg.service.aoi;

import lombok.Data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class GridCell {
    private Set<Integer> players = ConcurrentHashMap.newKeySet();


}
