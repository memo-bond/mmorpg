package bond.memo.mmorpg.adapter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class KeyListenerAdapter {

    public static KeyListener adapter(KeyAction action) {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                action.execute(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // No-op
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // No-op
            }
        };
    }
}