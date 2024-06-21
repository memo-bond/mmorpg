using NativeWebSocket;
using UnityEngine;
using Google.Protobuf;
using System.IO;
using System.Threading.Tasks;

public class WebSocketClient
{
    private readonly WebSocket websocket;

    public WebSocketClient(string url)
    {
        websocket = new WebSocket(url);

        websocket.OnOpen += () => Debug.Log("Connection open!");

        websocket.OnError += (e) => Debug.Log("Error! " + e);

        websocket.OnClose += (e) => Debug.Log("Connection closed!");

        websocket.OnMessage += (bytes) => OnMessage(bytes);
    }

    public async Task Connect()
    {
        await websocket.Connect();
    }

    public async Task Send(IMessage msg)
    {
        if (websocket.State == WebSocketState.Open)
        {
            byte[] bytes;
            using (var stream = new MemoryStream())
            {
                msg.WriteTo(stream);
                bytes = stream.ToArray();
            }
            await websocket.Send(bytes);
        }
    }

    private void OnMessage(byte[] bytes)
    {
        Debug.Log(bytes);

        // getting the message as a string
        var message = System.Text.Encoding.UTF8.GetString(bytes);
        Debug.Log("OnMessage! " + message);
    }

    public async Task Close()
    {
        await websocket.Close();
    }

    public WebSocketState State()
    {
        return websocket.State;
    }
}
