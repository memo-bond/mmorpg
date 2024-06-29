using System;
using System.IO;
using System.Threading.Tasks;
using Google.Protobuf;
using NativeWebSocket;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Connection
{
    public class WebSocketClientHandler : MonoBehaviour
    {
        [SerializeField] private bool local;
        public event Action<byte[]> OnMessageReceived;
        private WebSocket _webSocket;
        
        private async void Start()
        {
            try
            {
                _webSocket = local ? new WebSocket("ws://localhost/ws") : new WebSocket("wss://ws.memo.bond");
            }
            catch (Exception e)
            {
                Debug.LogError("Could not create connect to server due to " + e.Message);
            }
            _webSocket.OnMessage += (bytes) =>
            {
                if (bytes != null)
                {
                    try
                    {
                        OnMessageReceived?.Invoke(bytes);
                    }
                    catch (Exception ex)
                    {
                        Debug.LogError("Failed to parse message: " + ex.Message);
                    }
                }
                else
                {
                    Debug.LogWarning("Received non-binary message.");
                }
            };

            await Connect();
            Debug.Log("Connected To Server");
        }

        private async Task Connect()
        {
            _ = _webSocket.Connect();

            while (_webSocket.State != WebSocketState.Open)
            {
                await Task.Delay(100);
            }
        }

        public WebSocketState State()
        {
            return _webSocket.State;
        }

        private void Update()
        {
#if !UNITY_WEBGL || UNITY_EDITOR
            _webSocket.DispatchMessageQueue();
#endif
        }

        public async Task Send(IMessage msg)
        {
            if (_webSocket.State == WebSocketState.Open)
            {
                byte[] bytes;
                using (var stream = new MemoryStream())
                {
                    msg.WriteTo(stream);
                    bytes = stream.ToArray();
                }
                await _webSocket.Send(bytes);
            }
        }

        private async void OnApplicationQuit()
        {
            await _webSocket.Close();
        }
    }
}