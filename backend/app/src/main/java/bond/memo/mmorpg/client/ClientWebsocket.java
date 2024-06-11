package bond.memo.mmorpg.client;

import bond.memo.mmorpg.models.PlayerActions;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class ClientWebsocket extends WebSocketClient {

    public static void main(String[] args) {
        try {
            URI uri = new URI("ws://localhost:6666/ws");
            ClientWebsocket client = new ClientWebsocket(uri);
            client.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClientWebsocket(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Opened connection");

        // Create a Protobuf message
        PlayerActions.PlayerMessage msg = PlayerActions.PlayerMessage.newBuilder()
                .setJoin(PlayerActions.Join.newBuilder()
                        .setId(1)
                        .setName("Lucas")
                        .setX(123)
                        .setY(456)
                        .build())
                .build();

        // Serialize the message to byte array
        byte[] messageBytes = msg.toByteArray();

        System.out.println("send msg");
        // Send the serialized message
        send(messageBytes);
    }

    @Override
    public void onMessage(String s) {
        System.out.println("msg : " + s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {

    }
}
