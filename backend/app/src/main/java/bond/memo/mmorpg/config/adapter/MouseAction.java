package bond.memo.mmorpg.config.adapter;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface MouseAction {
    void execute(MouseEvent e);
}
