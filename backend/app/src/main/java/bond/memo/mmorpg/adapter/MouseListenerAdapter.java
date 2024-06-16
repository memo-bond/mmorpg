package bond.memo.mmorpg.adapter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class MouseListenerAdapter {

    private MouseListenerAdapter() {

    }

    public static MouseListener adapter(MouseAction action) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.execute(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
