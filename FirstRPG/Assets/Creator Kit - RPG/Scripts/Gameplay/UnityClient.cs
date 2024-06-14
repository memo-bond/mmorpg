using System;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Google.Protobuf;
using UnityEngine;
using static ByteUtils;

public class UnityClient
{
    private readonly string serverIP;
    private readonly int serverPort;
    private TcpClient client;
    private NetworkStream stream;
    private Thread clientThread;

    public UnityClient(string ip, int port)
    {
        serverIP = ip;
        serverPort = port;
    }

    public async Task<bool> ConnectAsync(IMessage join)
    {
        try
        {
            client = new TcpClient();
            await client.ConnectAsync(serverIP, serverPort);

            stream = client.GetStream();

            clientThread = new(() =>
            {
                _ = Task.Run(async () =>
                  {
                      await ReceiveData();
                  });
            });
            clientThread.IsBackground = true;
            clientThread.Start();

            Debug.Log("Join to server");
            _ = SendMessage(join);
            return true;
        }
        catch (Exception e)
        {
            Debug.Log("Error: " + e.Message);
            return false;
        }
    }

    public async Task SendMessage(IMessage message)
    {
        if (client == null || !client.Connected)
        {
            Debug.LogError("Client is not connected to the server.");
            Debug.Log("Try to connect again");

            await ConnectAsync(message);

            return;
        }

        try
        {
            byte[] data = massageMsg(message);

            // Send the message with length prefix
            await stream.WriteAsync(data, 0, data.Length);
        }
        catch (Exception e)
        {
            Debug.LogError("Error sending message to server: " + e.Message);
        }
    }


    private async Task ReceiveData()
    {
        try
        {
            while (client.Connected)
            {
                Debug.Log("Client connected : " + client.Connected);

                Debug.Log("stream data available : " + stream.DataAvailable);

                if (stream.DataAvailable)
                {
                    
                    // Read the message length prefix (assuming it's a fixed-size int)
                    byte[] lengthBytes = new byte[sizeof(int)];
                    int bytesRead = await stream.ReadAsync(lengthBytes, 0, lengthBytes.Length);
                    if (bytesRead < lengthBytes.Length)
                    {
                        Debug.LogError("Failed to read message length prefix.");
                        continue;
                    }

                    int messageLength = BitConverter.ToInt32(lengthBytes, 0);

                    // Read the message body
                    byte[] messageBytes = new byte[messageLength];
                    bytesRead = await stream.ReadAsync(messageBytes, 0, messageLength);
                    if (bytesRead < messageLength)
                    {
                        Debug.LogError("Failed to read the entire message.");
                        continue;
                    }

                    // Handle the received message
                    Debug.Log("Received from server: " + Encoding.UTF8.GetString(messageBytes));
                }
                await Task.Delay(1000); // Add a short delay to avoid tight loop
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Error receiving data from server: " + e.Message);
        }
    }



    public void Disconnect()
    {
        if (clientThread != null)
        {
            clientThread.Abort();
        }
        stream.Close();
        client.Close();
        Debug.Log("Disconnected from server");
    }
}
