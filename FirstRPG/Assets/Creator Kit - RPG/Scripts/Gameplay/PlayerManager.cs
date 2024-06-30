using System;
using System.Collections.Generic;
using Creator_Kit___RPG.Scripts.Character;
using Creator_Kit___RPG.Scripts.Core;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Gameplay
{
    public class PlayerManager : MonoBehaviour
    {
        [SerializeField] private GameObject otherCharacterPrefab;
        private readonly Dictionary<uint, BasePlayer> _players = new();

        private void Start()
        {
            EventManager.StartListening(EventName.PlayerJoined,
                obj =>
                {
                    var main = obj as MainPlayer;
                    if (main == null) return;
                    _players.Add(main.Id, main);
                });
        }

        public bool NotExists(uint id)
        {
            return !_players.ContainsKey(id);
        }

        /**
         * already guard by NotExists
         */
        public void AddPlayer(Join join)
        {
            if (_players.ContainsKey(join.Id)) return; // guard
            try
            {
                Debug.Log($"Init new player in our world `{join}`");
                var position = new Vector3(join.X, join.Y, 0);
                var playerController = Instantiate(otherCharacterPrefab, position, Quaternion.identity);
                var player = playerController.GetComponent<OtherPlayer>();
                player.Id = join.Id;
                player.Name = join.Name;
                player.Position = position;

                _players.Add(join.Id, player);
            }
            catch (Exception e)
            {
                Debug.LogError($"Could not add other player due to {e}");
            }
        }

        public void UpdatePlayerPosition(Move move)
        {
            if (!_players.TryGetValue(move.Id, out var p)) return;
            Debug.Log($"move.Direction {move.Direction}");
            if (p is not OtherPlayer player) return;
            var dest = new Vector3(move.X, move.Y, 0);
            switch (move.Direction)
            {
                case MoveDirection.Left:
                    player.MovePlayer(Vector3.left * Constants.StepSize, dest);
                    break;
                case MoveDirection.Right:
                    player.MovePlayer(Vector3.right * Constants.StepSize, dest);
                    break;
                case MoveDirection.Up:
                    player.MovePlayer(Vector3.up * Constants.StepSize,dest);
                    break;
                case MoveDirection.Down:
                    player.MovePlayer(Vector3.down * Constants.StepSize,dest);
                    break;
                default:
                    player.MovePlayer(Vector3.zero, Vector3.zero);
                    break;
            }
        }

        public void RemovePlayer(uint id)
        {
            if (!_players.TryGetValue(id, out var controller)) return;
            Destroy(controller.gameObject);
            _players.Remove(id);
        }
    }
}