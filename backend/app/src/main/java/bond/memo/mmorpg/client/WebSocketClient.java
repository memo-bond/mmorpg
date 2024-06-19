package bond.memo.mmorpg.client;

import bond.memo.mmorpg.models.PlayerActions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class WebSocketClient {

    private WebSocketListener webSocketListener;
    @Getter
    private WebSocket websocket;

    public static WebSocketClient of() {
        return new WebSocketClient();
    }

    public boolean isConnected() {
        return webSocketListener.isConnected();
    }

    public void send(byte[] data) {
        Objects.requireNonNull(data, "Websocket Data could not be NULL");
        websocket.sendBinary(ByteBuffer.wrap(data), true);
    }

    private WebSocketClient() {
        new Thread(() -> {
            webSocketListener = new WebSocketListener();

            try (HttpClient client = HttpClient.newHttpClient()) {
                log.info("wait 500ms for server started");
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));
                websocket = client.newWebSocketBuilder()
                        .buildAsync(URI.create("ws://127.0.0.1:6666/ws"), webSocketListener).join();
            } catch (Exception e) {
                log.error("Websocket client error due to ", e);
            }
        }).start();
    }

    @Getter
    @Slf4j
    static class WebSocketListener implements Listener {

        private volatile boolean connected;

        @Override
        public void onOpen(WebSocket webSocket) {
            log.info("WebSocket Client connected");
            webSocket.sendText("Hello, WebSocket!", true);
            Listener.super.onOpen(webSocket);
            this.connected = true;
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            log.info("WebSocket Client received message: {}", data);
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.error("Websocket ERROR ", error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            log.info("WebSocket Client disconnected: {}", reason);
            return Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}

