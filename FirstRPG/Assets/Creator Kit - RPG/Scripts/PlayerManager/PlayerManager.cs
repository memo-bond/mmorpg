using System.Collections.Generic;
using RPGM.Gameplay;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.PlayerManager
{
    public class PlayerManager : MonoBehaviour
    {
        [SerializeField] private GameObject playerPrefab;
        private readonly Dictionary<uint, CharacterController2D> players = new();

        public bool AddPlayer(Move move)
        {
            if (players.ContainsKey(move.Id))
            {
                Debug.LogWarning("Player with this ID already exists!");
                return false;
            }

            Debug.Log($"Init new player ID `{move.Id}` in our world");
            var position = new Vector3(move.X, move.Y, 0);
            var playerController = Instantiate(playerPrefab, position, Quaternion.identity);
            var player = playerController.GetComponent<CharacterController2D>();

            player.Id = move.Id;
            player.Name = move.Name;
            player.Position = position;
            player.Main = false;

            players.Add(move.Id, player);
            return true;
        }

        public void UpdatePlayerPosition(Move move)
        {
            if (players.TryGetValue(move.Id, out CharacterController2D player))
            {
                player.transform.position = new Vector3(move.X, move.Y, 0);
                Debug.Log($"move.Direction {move.Direction}");
                switch (move.Direction)
                {
                    case MoveDirection.Left:
                        player.nextMoveCommand = new Vector3(-0.1f, 0, 0);
                        break;
                    case MoveDirection.Right:
                        player.nextMoveCommand = new Vector3(0.1f, 0, 0);
                        break;
                    case MoveDirection.Up:
                        player.nextMoveCommand = new Vector3(0, 0.1f, 0);
                        break;
                    case MoveDirection.Down:
                        player.nextMoveCommand = new Vector3(0, -0.1f, 0);
                        break;
                }
            }
        }

        public void RemovePlayer(uint id)
        {
            if (players.TryGetValue(id, out CharacterController2D controller))
            {
                Destroy(controller.gameObject);
                players.Remove(id);
            }
        }
    }
}