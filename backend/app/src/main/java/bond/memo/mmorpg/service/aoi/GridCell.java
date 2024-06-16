package bond.memo.mmorpg.service.aoi;

import bond.memo.mmorpg.model.Player;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class GridCell {
    private List<Player> players = new CopyOnWriteArrayList<>();
}
