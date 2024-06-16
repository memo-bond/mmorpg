package bond.memo.mmorpg.config.adapter;

import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Slf4j
public final class KeyListenerAdapter {

    private KeyListenerAdapter() {
    }

    public static KeyListener adapter(KeyAction action) {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                action.execute(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };
    }
}
