using System.Threading.Tasks;
using Google.Protobuf;
using NativeWebSocket;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Connection
{
    public class ConnectionManager : MonoBehaviour
    {
        [SerializeField] private PlayerManager.PlayerManager playerManager;
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
            var msg = PlayerMessage.Parser.ParseFrom(data);
            Debug.Log("Received msg from server " + msg);
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
            // Debug.Log($"Response From Server: {msg}");
        }

        private void HandleJoinMessage(Join join)
        {
            Move move = new()
            {
                Id = join.Id,
                Name = join.Name,
                X = join.X,
                Y = join.Y
            };
            playerManager.AddPlayer(move);
        }

        private void HandleMoveMessage(Move move)
        {
            Debug.Log($"Player move {move.Id}-X={move.X}-Y={move.Y}");

            var addPlayer = playerManager.AddPlayer(move);
            if (addPlayer)
            {
                Debug.Log($"Player ID {move.Id} already added, update move only");
            }

            playerManager.UpdatePlayerPosition(move);
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