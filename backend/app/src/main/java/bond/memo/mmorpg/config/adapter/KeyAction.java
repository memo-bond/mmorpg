package bond.memo.mmorpg.config.adapter;

import java.awt.event.KeyEvent;

@FunctionalInterface
public interface KeyAction {
    void execute(KeyEvent e);
}
