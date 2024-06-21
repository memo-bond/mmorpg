using System;
using System.Collections.Generic;
using System.IO;
using NativeWebSocket;
using UnityEngine;
using System.Threading.Tasks;
using Google.Protobuf;
using RPGM.Gameplay;

namespace Creator_Kit___RPG.Scripts.Connection
{
    public class WebSocketClientHandler : MonoBehaviour
    {
        public event Action<byte[]> OnMessageReceived;
        private WebSocket _webSocket;
        private readonly Dictionary<int, CharacterController2D> _players = new();

        private async void Start()
        {
            Debug.Log("Start Connect To Server");
            _webSocket = new WebSocket("ws://localhost/ws");

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
                Debug.Log("Send Msg To Server");
                await _webSocket.Send(bytes);
            }
        }

        public void RegisterPlayer(int id, CharacterController2D controller)
        {
            _players[id] = controller;
        }

        public void UnregisterPlayer(int id)
        {
            _players.Remove(id);
        }

        public CharacterController2D GetPlayerController(int id)
        {
            _players.TryGetValue(id, out var controller);
            return controller;
        }

        private async void OnApplicationQuit()
        {
            await _webSocket.Close();
        }
    }
}