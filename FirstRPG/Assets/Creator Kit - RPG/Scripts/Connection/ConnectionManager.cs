using System.Text;
using System.Threading.Tasks;
using Google.Protobuf;
using NativeWebSocket;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Connection
{
    public class ConnectionManager : MonoBehaviour
    {
        [SerializeField] private PlayerManager playerManager;
        [SerializeField] private WebSocketClientHandler client;

        private void Start()
        {
            client.OnMessageReceived += HandleServerMessage;
        }
        
        private void OnDestroy()
        {
            if (client != null)
            {
                client.OnMessageReceived -= HandleServerMessage;
            }
        }
        
        public async Task Send(IMessage msg)
        {
            await client.Send(msg);
        }

        public WebSocketState State()
        {
            return client.State();
        }

        private void HandleServerMessage(byte[] data)
        {
            Debug.Log($"Data length {data.Length} ");
            var msg = PlayerMessage.Parser.ParseFrom(data);
            switch (msg.ActionCase)
            {
                case PlayerMessage.ActionOneofCase.Join:
                    HandleJoinMessage(msg.Join);
                    break;
                case PlayerMessage.ActionOneofCase.Move:
                    HandleMoveMessage(msg.Move);
                    break;
                case PlayerMessage.ActionOneofCase.Quit:
                    HandleQuitMessage(msg.Quit);
                    break;
                case PlayerMessage.ActionOneofCase.Leave:
                    HandleLeaveMessage(msg.Leave);
                    break;
                case PlayerMessage.ActionOneofCase.Response:
                    HandleResponseMessage(msg.Response);
                    break;
                case PlayerMessage.ActionOneofCase.None:
                    Debug.LogWarning("Message action None - not process");
                    break;
                default:
                    Debug.LogWarning("Unknown message type received.");
                    break;
            }
        }

        private void HandleResponseMessage(Response msg)
        {
            Debug.Log($"Response From Server: {msg}");
        }

        private void HandleJoinMessage(Join join)
        {
            playerManager.AddPlayer(join.Id, join.Name, new Vector3(join.X, join.Y, 0));
        }

        private void HandleMoveMessage(Move move)
        {
            playerManager.UpdatePlayerPosition(move.Id, new Vector3(move.X, move.Y, 0));
            
            var player = client.GetPlayerController(move.Id);
            if (player != null)
            {
                player.Position = new Vector3(move.X, move.Y, 0);
            }
        }

        private void HandleQuitMessage(Quit quit)
        {
            playerManager.RemovePlayer(quit.Id);
        }

        private void HandleLeaveMessage(Leave leave)
        {
            playerManager.RemovePlayer(leave.Id);
        }
    }
}