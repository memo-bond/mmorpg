package bond.memo.mmorpg.visualizer;

import bond.memo.mmorpg.client.WebSocketClient;
import bond.memo.mmorpg.model.Player;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
class ClientJoinHandler implements Runnable {

    private final WebSocketClient client;
    private final Queue<Player> queue;
    private volatile boolean running = true;

    public ClientJoinHandler(WebSocketClient client, Queue<Player> queue) {
        this.client = client;
        this.queue = queue;
    }

    public void addPlayer(Player player) {
        this.queue.add(player);
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
        while (running) {
            while (!queue.isEmpty() && client != null) {
                Player p = queue.poll();
//                log.info("JoinServer player join {}", p);
                if (p != null)
                    client.send(p.joinMsgBytes());
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(10));
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        }
    }
}
