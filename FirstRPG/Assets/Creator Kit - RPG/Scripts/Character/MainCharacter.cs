using System.Collections;
using Creator_Kit___RPG.Scripts.Connection;
using Creator_Kit___RPG.Scripts.Core;
using NativeWebSocket;
using UnityEngine;
using Random = UnityEngine.Random;

namespace Creator_Kit___RPG.Scripts.Character
{
    /// <summary>
    /// A simple controller for animating a 4 directional sprite using Physics.
    /// </summary>
    public class MainCharacter : BaseCharacter
    {
        [SerializeField] private bool online = false;
        [SerializeField] private ConnectionManager client;

        protected override void Start()
        {
            base.Start();

            if (!online) return;
            Id = (uint)Random.Range(1, 99);
            Name = "Lucas";
            PlayerMessage msg = new()
            {
                Join = new()
                {
                    Id = Id,
                    Name = Name,
                    X = transform.position.x,
                    Y = transform.position.y,
                    Unity = true
                }
            };
            StartCoroutine(SendJoinMsg(msg));
        }

        private IEnumerator SendJoinMsg(PlayerMessage msg)
        {
            var timeout = 10f; // Timeout after 10 seconds
            var elapsedTime = 0f;

            while (client.State() != WebSocketState.Open && elapsedTime < timeout)
            {
                Debug.Log("Check State " + client.State());
                yield return new WaitForSeconds(0.3f);
                elapsedTime += 0.3f;
            }

            if (client.State() == WebSocketState.Open)
            {
                Debug.Log($"Main Player Instance ID {GetInstanceID()}");
                Debug.Log("Send Join Msg To Server " + msg);
                _ = client.Send(msg);
                EventManager.TriggerEvent(EventName.PlayerJoined, this);
            }
            else
            {
                Debug.LogError("WebSocket connection failed to open within timeout period.");
            }
        }

        protected override void MoveState()
        {
            base.MoveState();

            if (!online || !(Vector3.Distance(transform.position, lastPosition) > positionUpdateThreshold)) return;

            SendMoveMsg();
            lastPosition = transform.position;
        }

        private void SendMoveMsg()
        {
            Debug.Log($"Main Player Instance ID {GetInstanceID()}");
            Debug.Log($"ID `{Id}` Name `{Name}`");
            PlayerMessage msg = new()
            {
                Move = new()
                {
                    Id = Id,
                    Name = Name,
                    X = transform.position.x,
                    Y = transform.position.y
                }
            };
            Debug.Log($"Player Move {msg}");
            _ = client.Send(msg);
        }

        private async void OnApplicationQuit()
        {
            PlayerMessage msg = new()
            {
                Quit = new()
                {
                    Id = Id
                }
            };
            await client.Send(msg);
        }
    }
}