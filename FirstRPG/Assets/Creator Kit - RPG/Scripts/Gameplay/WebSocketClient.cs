using System;
using System.IO;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Google.Protobuf;
using UnityEngine;

public class WebSocketClient
{
    private ClientWebSocket webSocket = null;
    private readonly Uri serverUri = new("ws://127.0.0.1:6666/ws");

    public WebSocketClient()
    {
        webSocket = new ClientWebSocket();
    }

    public async Task ConnectWebSocket()
    {
        try
        {
            await webSocket.ConnectAsync(serverUri, CancellationToken.None);
            Debug.Log("WebSocket connected!");

            // Start receiving data
             ReceiveWebSocketData();
        }
        catch (Exception e)
        {
            Debug.LogError($"WebSocket connection failed: {e.Message}");
        }
    }

    public async Task SendMessage(IMessage message)
    {
        if (webSocket.State == WebSocketState.Open)
        {
            byte[] bytes;
            using (var stream = new MemoryStream())
            {
                message.WriteTo(stream);
                bytes = stream.ToArray();
            }

            await webSocket.SendAsync(new ArraySegment<byte>(bytes), WebSocketMessageType.Binary, true, CancellationToken.None);
        }
    }

    private async void ReceiveWebSocketData()
    {
        var buffer = new byte[1024];

        while (webSocket.State == WebSocketState.Open)
        {
            var result = await webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), CancellationToken.None);

            if (result.MessageType == WebSocketMessageType.Close)
            {
                await webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, string.Empty, CancellationToken.None);
                Debug.Log("WebSocket closed");
            }
            else
            {
                var message = Encoding.UTF8.GetString(buffer, 0, result.Count);
                Debug.Log($"Received: {message}");
            }
        }
    }

    private async void OnApplicationQuit()
    {
        if (webSocket != null)
        {
            await webSocket.CloseAsync(WebSocketCloseStatus.NormalClosure, "Application exiting", CancellationToken.None);
            webSocket.Dispose();
            webSocket = null;
            Debug.Log("WebSocket closed");
        }
    }
}
