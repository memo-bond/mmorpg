using System;
using System.Collections.Generic;
using Creator_Kit___RPG.Scripts.Character;
using Creator_Kit___RPG.Scripts.Core;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Gameplay
{
    public class PlayerManager : MonoBehaviour
    {
        [SerializeField] private GameObject characterPrefab;
        private readonly Dictionary<uint, BaseCharacter> _players = new();

        private void Start()
        {
            EventManager.StartListening(EventName.PlayerJoined,
                obj =>
                {
                    var main = obj as MainCharacter;
                    if (main == null) return;
                    _players.Add(main.Id, main);
                });
        }

        public bool PlayerNotExists(uint id)
        {
            return !_players.ContainsKey(id);
        }

        /**
         * already guard from PlayerNotExists
         */
        public bool AddPlayer(Join join)
        {
            try
            {
                Debug.Log($"Init new player in our world `{join}`");
                var position = new Vector3(join.X, join.Y, 0);
                var playerController = Instantiate(characterPrefab, position, Quaternion.identity);
                var player = playerController.GetComponent<OtherPlayer>();
                player.Id = join.Id;
                player.Name = join.Name;
                player.Position = position;
                player.Main = false;

                _players.Add(join.Id, player);
            }
            catch (Exception e)
            {
                Debug.LogError($"Could not add other player due to {e}");
                return false;
            }

            return true;
        }

        public void UpdatePlayerPosition(Move move)
        {
            if (_players.TryGetValue(move.Id, out var player))
            {
                Debug.Log($"move.Direction {move.Direction}");
                float moveSpeed = 10.0f;
                switch (move.Direction)
                {
                    case MoveDirection.Left:
                        player.MovePlayer(new Vector3(-moveSpeed * Time.deltaTime, 0, 0));
                        break;
                    case MoveDirection.Right:
                        player.MovePlayer(new Vector3(moveSpeed * Time.deltaTime, 0, 0));
                        break;
                    case MoveDirection.Up:
                        player.MovePlayer(new Vector3(0, moveSpeed * Time.deltaTime, 0));
                        break;
                    case MoveDirection.Down:
                        player.MovePlayer(new Vector3(0, -moveSpeed * Time.deltaTime, 0));
                        break;
                }
            }
        }

        public void RemovePlayer(uint id)
        {
            if (_players.TryGetValue(id, out var controller))
            {
                Destroy(controller.gameObject);
                _players.Remove(id);
            }
        }
    }
}