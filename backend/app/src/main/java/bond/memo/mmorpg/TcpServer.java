package bond.memo.mmorpg;

import java.io.*;
import java.net.*;

public class TcpServer {

    public static void main(String[] args) {
        int port = 6666; // Define the port the server will listen on

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");

                    // InputStream to receive data from the client
                    InputStream inputStream = socket.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    // Reading bytes from the client
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                        // Process the bytes received (for demonstration, we'll just print them)
                        System.out.println("Received " + bytesRead + " bytes from client");
                        String receivedData = new String(buffer, 0, bytesRead);
                        System.out.println("Data: " + receivedData);
                    }
                } catch (IOException ex) {
                    System.out.println("Server exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        } catch (IOException ex) {
            System.out.println("Could not listen on port " + port);
            ex.printStackTrace();
        }
    }
}
