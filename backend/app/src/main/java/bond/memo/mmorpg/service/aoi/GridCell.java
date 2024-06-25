package bond.memo.mmorpg.service.aoi;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class GridCell {
    private List<Integer> players = new CopyOnWriteArrayList<>();


}
