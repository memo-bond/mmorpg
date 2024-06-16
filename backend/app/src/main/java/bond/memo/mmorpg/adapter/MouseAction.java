package bond.memo.mmorpg.adapter;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface MouseAction {
    void execute(MouseEvent e);
}
